package AutoEnchanter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GeItem;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Bank.Amount;
import org.powerbot.script.rt4.Magic.Spell;

@Script.Manifest(name = "Dittymaster's Enchanter", description = "Enchants Jewellery", properties = "author=Dittymaster;topic=1319500;client=4")
public class AutoEnchant extends PollingScript<ClientContext> implements PaintListener {
	private static GUI form;
	private String gemEnchant;
	private String jeweleryType;
	private int startLevel = ctx.skills.level(6);
	private int startExp = ctx.skills.experience(6);
	private long startTime = System.currentTimeMillis();
	private Resources rsc;
	private Spell currentSpell;
	private int jewID;
	private int cosmicID = 564;

	@Override
	public void start() {
		form = new GUI(this);
		form.isAlwaysOnTop();
		form.setLocationRelativeTo(null);
		form.setVisible(true);
		while (!form.valid) {
			Condition.sleep(500);
		}
		jeweleryType = form.jewelery;
		gemEnchant = form.gem;
		System.out.println("Gem:" + gemEnchant);
		rsc = new Resources();

		getEnchant();
		getJewelery();

	}

	@Override
	public void poll() {
		// TODO Auto-generated method stub
		final State state = getState();

		if (state == null) {
			return;
		}

		switch (state) {
		case ENCHANT:
			System.out.println("Enchanting State");
			if (ctx.players.local().animation() == -1) {
				final Item jewelery = ctx.inventory.select().id(jewID).poll();
				if (ctx.inventory.select().id(cosmicID).count() == 0) {
					log.info("OUT OF RUNES GET MOAR");
					ctx.controller.stop();
				}
				if (ctx.magic.cast(currentSpell)) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							// TODO Auto-generated method stub
							return ctx.magic.casting(currentSpell);
						}
					}, 250, 30);
				}
				jewelery.interact(jewelery.actions()[0]);
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						// TODO Auto-generated method stub
						return ctx.inventory.select().id(jewID).count() == 0 || ctx.chat.canContinue();
					}
				}, 1000, 20);

			}

			break;

		case BANK:
			if (ctx.magic.casting(currentSpell))
				ctx.input.click(true);

			if (ctx.bank.open()) {
				if (ctx.bank.select().id(jewID).count(true) < 10) {
					ctx.bank.depositAllExcept(cosmicID);
					log.info("Please get more materials");
					ctx.controller.stop();
				} else {
					ctx.bank.depositAllExcept(cosmicID);
					if (ctx.inventory.select().id(cosmicID).count() == 0) {
						ctx.bank.withdraw(cosmicID, Amount.ALL);
					}
					ctx.bank.withdraw(jewID, Amount.ALL);
					ctx.bank.close();

				}
			}

			break;

		}

	}

	private AutoEnchant.State getState() {
		if (ctx.inventory.select().id(jewID).count() > 0) {
			return State.ENCHANT;
		} else
			return State.BANK;

	}

	private enum State {
		ENCHANT, BANK;
	}

	public void getEnchant() {
		if (gemEnchant.equals("Sapphire")) {
			currentSpell = Spell.ENCHANT_LEVEL_1_JEWELLERY;
		} else if (gemEnchant.equals("Emerald")) {
			currentSpell = Spell.ENCHANT_LEVEL_2_JEWELLERY;
		} else if (gemEnchant.equals("Ruby")) {
			currentSpell = Spell.ENCHANT_LEVEL_3_JEWELLERY;
		} else if (gemEnchant.equals("Diamond")) {
			currentSpell = Spell.ENCHANT_LEVEL_4_JEWELLERY;
		} else if (gemEnchant.equals("Dragonstone")) {
			currentSpell = Spell.ENCHANT_LEVEL_5_JEWELLERY;
		} else if (gemEnchant.equals("Onyx")) {
			currentSpell = Spell.ENCHANT_LEVEL_6_JEWELLERY;
		} else if (gemEnchant.equals("Zenyte")) {
			currentSpell = Spell.ENCHANT_LEVEL_7_JEWELLERY;
		}
	}

	public void getJewelery() {
		jewID = rsc.map.get(gemEnchant + jeweleryType);

	}

	@Override
	public void repaint(Graphics graphics) {
		// TODO Auto-generated method stub

		final Graphics2D g = (Graphics2D) graphics;
		Point mouse = ctx.input.getLocation();

		Color color = new Color(0, 0, 0, 200);
		g.setColor(color);
		g.fillRect(5, 5, 160, 85);
		g.setColor(Color.WHITE);

		g.setColor(Color.GREEN);
		g.drawLine((int) mouse.getX() - 10, (int) mouse.getY(), (int) mouse.getX() + 10, (int) mouse.getY());
		g.drawLine((int) mouse.getX(), (int) mouse.getY() - 10, (int) mouse.getX(), (int) mouse.getY() + 10);

		long runtime = System.currentTimeMillis() - startTime;
		java.lang.String hms = java.lang.String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runtime),
				TimeUnit.MILLISECONDS.toMinutes(runtime) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(runtime) % TimeUnit.MINUTES.toSeconds(1));

		g.drawString("Dittymaster's Enchanter", 10, 20);
		g.drawString("Runtime: " + hms, 10, 40);
		g.drawString("Level: " + startLevel + " + " + "(" + (ctx.skills.level(6) - startLevel) + ")", 10, 60);
		int expGained = ctx.skills.experience(6) - startExp;
		g.drawString("XP(XP/h): " + expGained + "(" + Math.round(((float) expGained / runtime) * 3600000) + ")", 10,
				80);
	}

	public void stop() {
		ctx.controller.stop();
	}
}
