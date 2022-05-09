package com.jacobtread.kme.blaze

object IdentifierLookups {
    val NotificationNames = mapOf(
        0x0004000A to "NotifyMatchmakingFailed",
        0x0004000C to "NotifyMatchmakingAsyncStatus",
        0x0004000F to "NotifyGameCreated",
        0x00040010 to "NotifyGameRemoved",
        0x00040014 to "NotifyGameSetup",
        0x00040015 to "NotifyPlayerJoining",
        0x00040016 to "NotifyJoiningPlayerInitiateConnections",
        0x00040017 to "NotifyPlayerJoiningQueue",
        0x00040018 to "NotifyPlayerPromotedFromQueue",
        0x00040019 to "NotifyPlayerClaimingReservation",
        0x0004001E to "NotifyPlayerJoinCompleted",
        0x00040028 to "NotifyPlayerRemoved",
        0x0004003C to "NotifyHostMigrationFinished",
        0x00040046 to "NotifyHostMigrationStart",
        0x00040047 to "NotifyPlatformHostInitialized",
        0x00040050 to "NotifyGameAttribChange",
        0x0004005A to "NotifyPlayerAttribChange",
        0x0004005F to "NotifyPlayerCustomDataChange",
        0x00040064 to "NotifyGameStateChange",
        0x0004006E to "NotifyGameSettingsChange",
        0x0004006F to "NotifyGameCapacityChange",
        0x00040070 to "NotifyGameReset",
        0x00040071 to "NotifyGameReportingIdChange",
        0x00040073 to "NotifyGameSessionUpdated",
        0x00040074 to "NotifyGamePlayerStateChange",
        0x00040075 to "NotifyGamePlayerTeamChange",
        0x00040076 to "NotifyGameTeamIdChange",
        0x00040077 to "NotifyProcessQueue",
        0x00040078 to "NotifyPresenceModeChanged",
        0x00040079 to "NotifyGamePlayerQueuePositionChange",
        0x000400C9 to "NotifyGameListUpdate",
        0x000400CA to "NotifyAdminListChange",
        0x000400DC to "NotifyCreateDynamicDedicatedServerGame",
        0x000400E6 to "NotifyGameNameChange"
    )
}