package it.ancientrealms.model

import org.bukkit.Bukkit
import java.io.Serializable
import java.util.*

data class Party(
    val leader: UUID,
    var password: String,
    val playerList: MutableSet<UUID>
) : Serializable{
    init {
        playerList.add(leader)
    }

    fun getPartyInfo(): String {
        val stringBuilder = StringBuilder()
            .append("&6Party leader: &a")
            .append(Bukkit.getServer().getOfflinePlayer(leader).name)
            .append("\n")
            .append("&6Members list: &a\n")
        for (player in playerList) {
            stringBuilder.append(Bukkit.getServer().getOfflinePlayer(player).name).append("\n")
        }
        return stringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Party

        if (leader != other.leader) return false

        return true
    }

    override fun hashCode(): Int {
        return leader.hashCode()
    }


}