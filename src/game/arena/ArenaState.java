package game.arena;

public enum ArenaState {
	WAITING_FOR_PLAYERS("ᴡᴀɪᴛɪɴɢ"),
	STARTING("sᴛᴀʀᴛɪɴɢ"),
	PREGAME("ᴘʀᴇɢᴀᴍᴇ"),
	IN_GAME("ғɪɢʜᴛɪɴɢ"),
	ENDING("ᴇɴᴅɪɴɢ"),
	RESTARTING("ʀᴇsᴛᴀʀᴛɪɴɢ"),
	INACTIVE("ɪɴᴀᴄᴛɪᴠᴇ");

	public final String name;

	ArenaState(String name) {
		this.name = name;
	}
}
