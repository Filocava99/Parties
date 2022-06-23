# Parties
A lightweight Spigot plugin for managing parties.

## Commands
### Basic commands
* `/party`: Opens the party menu.
* `/party create <password>`: creates a party with the value of `<password>` as password.
* `/party disband`: disbands a party
* `/party setPassword <password>`: set the new party password to the value of `<password>`
* `/party join <partyLeaderName> <password>`: join the party of `<partyLeaderName>` with the provided password `<password>`
* `/party invite <playerName>`: invites `<playername>` to the party
* `/party accept`: accepts a party invitation
* `/party decline`: declines a party invitation
* `/party leave`: leaves a party
* `/party kick <player>`: kick `<player>` from the party
* `/party chat [message]>` or a`/pc [message]`: sends a message to the party chat
* `/party info`: displays the party info
* `/pos` sends your position in the party chat
* `/pc` or `/partychat` enables/disables the party chat

### Admin commands
***Command aliases: `[/partyadmin, /pa]`***
* `/pa spy`: spies the parties chats
* `/pa reload`: reloads the plugin config

## Permissions
### Basic permissions
* `parties.party.accept` for `/party accept`
* `parties.party.chat` for `/party chat`
* `parties.party.create` for `/party create`
* `parties.party.decline` for `/party decline`
* `parties.party.disband` for `/party disband`
* `parties.party.help` for `/party`
* `parties.party.info`for `/party info`
* `parties.party.invite` for `/party invite`
* `parties.party.join` for `/party join`
* `parties.party.kick` for `/party kick`
* `parties.party.leave` for `/party leave`
* `parties.party.password` for `/party setPassword`
* `parties.chat` for `/pc`
### Admin permissions
* `parties.admin` for all the admin commands
## Configuration
* `party_limit` sets the maximum number of players that can be in party. Defaults to `50`.
* `always_on_friendly_fire_worlds` sets the worlds that will always have friendly fire enabled. Defaults to `world-default`.
* You can translate the messages to your language. The only already translated config is for the [Italian language](https://github.com/Filocava99/Parties/blob/master/src/main/resources/config_IT.yml).