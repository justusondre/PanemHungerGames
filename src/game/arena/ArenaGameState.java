package game.arena;

public enum ArenaGameState {
	INACTIVE("ɴᴏ ᴇᴠᴇɴᴛ"),
	BLOODBATH("ʙʟᴏᴏᴅʙᴀᴛʜ"),
	CHEST_RESTOCK("ᴄʜᴇѕᴛ ʀᴇѕᴛᴏᴄᴋ"),
	THE_FALLEN("ᴛʜᴇ ғᴀʟʟᴇɴ"),
	ACID_RAIN("ᴀᴄɪᴅ ʀᴀɪɴ"),
	METEOR_SHOWER("ᴍᴇᴛᴇᴏʀ sʜᴏᴡᴇʀ"),
	POISON_GAS("ᴛᴏxɪᴄ ɢᴀs"),
	FINALE("ᴅᴇᴀᴛʜᴍᴀᴛᴄʜ");

	public final String name;

	ArenaGameState(String name) {
		this.name = name;
	}
}
