package com.jacobtread.kme.game

import com.jacobtread.kme.blaze.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class Game(
    val id: ULong,
    val mid: ULong,
    var host: PlayerSession,
) {

    companion object {
        const val MAX_PLAYERS = 4
        const val MIN_ID = 0x5DC695uL
        const val MIN_MID = 0x1129DA20uL
    }

    var gameState: Int = 0x1
    var gameSetting: Int = 0x11f

    private val attributesLock = ReentrantReadWriteLock()
    private val attributes = HashMap<String, String>()

    private var isActive = true

    private val players = ArrayList<PlayerSession>(MAX_PLAYERS)
    private val playersLock = ReentrantReadWriteLock()

    init {
        players.add(host)
    }

    fun isInActive(): Boolean = !isActive || !host.isActive

    fun isFull(): Boolean = playersLock.read { players.size >= MAX_PLAYERS }
    fun isJoinable(): Boolean = isActive && !isFull()
    fun getActivePlayers(): List<PlayerSession> = playersLock.read {
        players.filter { it.isActive }
            .toMutableList()
    }

    fun join(player: PlayerSession) = playersLock.write {
        players.add(player)
        player.game = this
        sendHostPlayerJoin(player)
    }

    private fun sendHostPlayerJoin(session: PlayerSession) {
        val player = session.playerEntity
        val sessionDetails = session.createSessionDetails()
        host.push(sessionDetails)
        host.pushUnique(Components.GAME_MANAGER, Commands.JOIN_GAME_BY_GROUP){
            number("GID", id)
            +group("PDAT") {
                blob("BLOB")
                number("EXID", 0x0)
                number("GID", id)
                number("LOC", 0x64654445)
                text("NAME", player.displayName)
                number("PID", player.playerId)
                +session.createAddrOptional("PNET")
                number("SID", players.size - 1)
                number("SLOT", 0x0)
                number("STAT", 0x2)
                number("TIDX", 0xffff)
                number("TIME", 0x0)
                tripple("UGID", 0x0, 0x0, 0x0)
                number("UID", player.playerId)
            }
        }
        host.push(session.createSetSession())
    }

    fun removePlayer(player: PlayerSession) {
        playersLock.write { players.remove(player) }
    }

    fun removePlayer(playerId: Int) {
        playersLock.read {
            val index = players.indexOfFirst { it.playerId == playerId }
            if (index != -1) {
                val player: PlayerSession = players[index]
                player.game = null
                playersLock.write {
                    players.removeAt(index)
                }
            }
            if (playerId == host.playerId) {
                if (players.isEmpty()) {
                    return stop()
                } else {
                    val first = players.firstOrNull()
                    if (first != null) {
                        host = first
                    } else {
                        return stop()
                    }
                }
            }
            val hostPacket = unique(Components.USER_SESSIONS, Commands.FETCH_EXTENDED_DATA) { number("BUID", host.playerId) }
            val hostContent = hostPacket.contentBuffer
            if (players.size > 1) hostContent.retain(players.size - 1)
            players.forEach {
                if (it != host) {
                    val userPacket = unique(Components.USER_SESSIONS, Commands.FETCH_EXTENDED_DATA) { number("BUID", it.playerId) }
                    it.push(hostPacket)
                    host.push(userPacket)
                }
            }

        }
    }

    private fun stop() {
        GameManager.releaseGame(this)
        isActive = false
        playersLock.write {
            players.removeIf {
                it.game = null
                true
            }
        }
    }

    fun broadcastAttributeUpdate() {
        playersLock.read {
            val packet = createNotifyPacket()
            if (players.size > 1) packet.contentBuffer.retain(players.size - 1)
            players.forEach { it.push(packet) }
        }
    }

    fun createNotifyPacket(): Packet =
        unique(
            Components.GAME_MANAGER,
            Commands.NOTIFY_GAME_UPDATED
        ) {
            map("ATTR", getAttributes())
            number("GID", id)
        }

    fun createPoolPacket(init: Boolean, forSession: PlayerSession): Packet =
        unique(
            Components.GAME_MANAGER,
            Commands.RETURN_DEDICATED_SERVER_TO_POOL
        ) {
            val playerIds = ArrayList<Long>()
            val pros = players.mapIndexed { index, playerSession ->
                val player = playerSession.playerEntity
                playerIds.add(player.playerId.toLong())
                group {
                    blob("BLOB")
                    number("EXID", 0x0)
                    number("GID", this@Game.id) // Game ID
                    number("LOC", 0x64654445) // Location
                    text("NAME", player.displayName) // Player name
                    number("PID", player.playerId) // Player id
                    +playerSession.createAddrOptional("PNET") // Player net info
                    number("SID", index) // Slot ID
                    number("SLOT", 0x0)
                    number("STAT", if (host.playerId == player.playerId) 0x4 else 0x2)
                    number("TIDX", 0xffff)
                    number("TIME", 0x0)
                    tripple("UGID", 0x0, 0x0, 0x0)
                    number("UID", player.playerId)
                }
            }

            val hostPlayer = host.playerEntity
            +group("GAME") {
                // Game Admins
                list("ADMN", playerIds)
                map("ATTR", getAttributes())
                list("CAP", listOf(0x4, 0x0))
                number("GID", id)
                text("GNAM", hostPlayer.displayName)
                number("GPVH", 0x5a4f2b378b715c6)
                number("GSET", gameSetting)
                number("GSID", 0x4000000a71dc21)
                number("GSTA", gameState)
                text("GTYP", "")
                // Host network information
                list("HNET", listOf(
                    group(start2 = true) {
                        +host.extNetData.createGroup("EXIP")
                        +host.intNetData.createGroup("INIP")
                    }
                ))
                number("HSES", 0x10f3e7f2)
                number("IGNO", 0x0)
                number("MCAP", 0x4)
                +group("NQOS") {
                    val otherNetData= host.otherNetData
                    number("DBPS", otherNetData.dbps)
                    number("NATT", otherNetData.natt)
                    number("UBPS", otherNetData.ubps)
                }
                number("NRES", 0x0)
                number("NTOP", 0x0)
                text("PGID", "")
                blob("PGSR")
                +group("PHST") {
                    number("HPID", hostPlayer.playerId)
                    number("HSLT", 0x0)
                }
                number("PRES", 0x1)
                text("PSAS", "")
                number("QCAP", 0x0)
                number("SEED", 0x4cbc8585) // Seed? Could be used for game randomness?
                number("TCAP", 0x0)
                +group("THST") {
                    number("HPID", hostPlayer.playerId)
                    number("HSLT", 0x0)
                }
                text("UUID", "286a2373-3e6e-46b9-8294-3ef05e479503")
                number("VOIP", 0x2)
                text("VSTR", "ME3-295976325-179181965240128") // Mass effect version string
                blob("XNNC")
                blob("XSES")
            }

            list("PROS", pros)
            if (init) {
                optional("REAS", group("VALU") {
                    number("DCTX", 0x0)
                })
            } else {
                optional("REAS", 0x3u, group("VALU") {
                    number("FIT", 0x3f7a)
                    number("MAXF", 0x5460)
                    number("MSID", mid)
                    number("RSLT", 0x2)
                    number("USID", forSession.playerId)
                })
            }
        }

    fun getAttributes(): Map<String, String> = attributesLock.read { attributes }

    fun setAttributes(map: Map<String, String>) {
        attributesLock.write { attributes.putAll(map) }
    }

}