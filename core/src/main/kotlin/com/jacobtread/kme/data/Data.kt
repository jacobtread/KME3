package com.jacobtread.kme.data

import com.jacobtread.kme.blaze.*

object Data {

    fun makeUserEntitlements2(id: Int): RawPacket {
        @Suppress("SpellCheckingInspection")
        return packet(PacketComponent.AUTHENTICATION, PacketCommand.LIST_USER_ENTITLEMENTS_2, 0x1000, id) {
            list("NLST", listOf(
                struct {
                    text("DEVI", "")
                    text("GDAY", "2013-03-04T22:16Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe962a115d7)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "300241")
                    number("PRCA", 0x2)
                    text("PRID", "OFB-MASS:59712")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ME3_PRC_MP5")
                    text("TDAY", "")
                    number("TYPE", 0x5)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-15T16:15Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe91655d5d7)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "300241")
                    number("PRCA", 0x2)
                    text("PRID", "OFB-MASS:47872")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ME3_PRC_RESURGENCE")
                    text("TDAY", "")
                    number("TYPE", 0x5)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-14T13:32Z")
                    text("GNAM", "ME3GenOffers")
                    number("ID  ", 0xe915dbc3d7)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "303107")
                    number("PRCA", 0x0)
                    text("PRID", "")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ONLINE_ACCESS_GAW_PC")
                    text("TDAY", "")
                    number("TYPE", 0x1)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-14T13:5Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe915aaefd7)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "300241")
                    number("PRCA", 0x2)
                    text("PRID", "OFB-MASS:51074")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ME3_PRC_EXTENDEDCUT")
                    text("TDAY", "")
                    number("TYPE", 0x5)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-14T13:5Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe915a7e297)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "308426")
                    number("PRCA", 0x2)
                    text("PRID", "OFB-EAST:56562")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "MET_BONUS_CONTENT")
                    text("TDAY", "")
                    number("TYPE", 0x5)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-14T13:5Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe915a1c817)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "303107")
                    number("PRCA", 0x2)
                    text("PRID", "DR:229644400")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "PROJECT10_CODE_CONSUMED")
                    text("TDAY", "")
                    number("TYPE", 0x1)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-14T13:5Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe9159ebad7)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "303107")
                    number("PRCA", 0x2)
                    text("PRID", "DR:229644400")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ONLINE_ACCESS")
                    text("TDAY", "")
                    number("TYPE", 0x1)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-15T16:16Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe910353b57)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "300241")
                    number("PRCA", 0x2)
                    text("PRID", "OFB-MASS:49465")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ME3_PRC_REBELLION")
                    text("TDAY", "")
                    number("TYPE", 0x5)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-15T16:16Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe90c3cff17)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "300241")
                    number("PRCA", 0x2)
                    text("PRID", "OFB-MASS:51073")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ME3_PRC_EARTH")
                    text("TDAY", "")
                    number("TYPE", 0x5)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                },
                struct {
                    text("DEVI", "")
                    text("GDAY", "2012-12-15T16:16Z")
                    text("GNAM", "ME3PCOffers")
                    number("ID  ", 0xe90b85e417)
                    number("ISCO", 0x0)
                    number("PID ", 0x0)
                    text("PJID", "300241")
                    number("PRCA", 0x2)
                    text("PRID", "OFB-MASS:52000")
                    number("STAT", 0x1)
                    number("STRC", 0x0)
                    text("TAG ", "ME3_PRC_GOBIG")
                    text("TDAY", "")
                    number("TYPE", 0x5)
                    number("UCNT", 0x0)
                    number("VER ", 0x0)
                }
            ))
        }
    }


}