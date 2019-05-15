package AutoEnchanter;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.ClientContext;

class Resources {

	String[] gemEnchant = { "Sapphire", "Emerald", "Ruby", "Diamond", "Dragonstone", "Onyx", "Zenyte" };
	String[] jeweleryType = { "Ring", "Necklace", "Bracelet", "Amulet" };
	HashMap<String, Integer> map = new HashMap<String, Integer>() {
		{
			put("SapphireRing", 1637);
			put("SapphireNecklace", 1656);
			put("SapphireBracelet", 11072);
			put("SapphireAmulet", 1675);

			put("EmeraldRing", 1639);
			put("EmeraldNecklace", 1658);
			put("EmeraldBracelet", 11076);
			put("EmeraldAmulet", 1677);

			put("RubyRing", 1641);
			put("RubyNecklace", 1660);
			put("RubyBracelet", 11085);
			put("RubyAmulet", 1679);

			put("DiamondRing", 1643);
			put("DiamondNecklace", 1662);
			put("DiamondBracelet", 11092);
			put("DiamondAmulet", 1681);

			put("DragonstoneRing", 1645);
			put("DragonstoneNecklace", 1664);
			put("DragonstoneBracelet", 11115);
			put("DragonstoneAmulet", 1702);

			put("OnyxRing", 6564);
			put("OnyxNecklace", 6565);
			put("OnyxBracelet", 11130);
			put("OnyxAmulet", 6566);

			put("ZenyteRing", 19538);
			put("ZenyteNecklace", 19535);
			put("ZenyteBracelet", 19492);
			put("ZenyteAmulet", 19541);

		}
	};
}
