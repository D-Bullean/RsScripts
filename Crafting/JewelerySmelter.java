package Crafting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.*;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;

@Script.Manifest(name = "Dittymaster's Crafter", description = "Crafts Gold Bars", properties = "author=Dittymaster;topic=1319500;client=4")

public class JewelerySmelter extends PollingScript<ClientContext> implements PaintListener {
	private Tile furnace_tile = new Tile(3109, 3499, 0);
	private Tile bank_tile = new Tile(3096, 3494, 0);
	private int startLevel = ctx.skills.level(12);
	private int startExp = ctx.skills.experience(12);
	private long startTime = System.currentTimeMillis();
	private static final Font newFont = new Font("Dialog", Font.BOLD, 12);
	private static Resources rsc;
	private int mouldId = 1592;

	private Image img = Toolkit.getDefaultToolkit()
			.createImage("C:/Users/Dittymaster/eclipse-workspace/Powerbot/src/runescapeBackground.jpg");

	private int lastXp;

	@Override

	public void start() {
		rsc = new Resources();

	}

	@Override
	public void repaint(Graphics graphics) {
		// TODO Auto-generated method stub
		final Graphics2D g = (Graphics2D) graphics;
		final Component chatBox = ctx.widgets.widget(162).component(40);
		final int chatX = chatBox.screenPoint().x;
		final int chatY = chatBox.screenPoint().y;

		Point mouse = ctx.input.getLocation();
		g.setFont(newFont);
		g.setColor(Color.RED);
		g.drawLine((int) mouse.getX() - 10, (int) mouse.getY(), (int) mouse.getX() + 10, (int) mouse.getY());
		g.drawLine((int) mouse.getX(), (int) mouse.getY() - 10, (int) mouse.getX(), (int) mouse.getY() + 10);

		long runtime = System.currentTimeMillis() - startTime;

		java.lang.String hms = java.lang.String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runtime),
				TimeUnit.MILLISECONDS.toMinutes(runtime) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(runtime) % TimeUnit.MINUTES.toSeconds(1));
		g.drawImage(img, chatX, chatY, null);
		g.drawString("Farquaad's Quad Smelter", 10, chatY + 30);
		g.drawString("Runtime: " + hms, 10, chatY + 50);
		g.drawString("Level: " + startLevel + "+" + "(" + (ctx.skills.level(12) - startLevel) + ")", 10, chatY + 70);
		int expGained = ctx.skills.experience(12) - startExp;
		g.drawString("Experience: " + expGained, 10, chatY + 90);
	}

	@Override
	public void poll() {
		// TODO Auto-generated method stub
		final State state = getState();
		if (state == null) {
			return;
		}

		switch (state) {
		case CRAFT:
			if (ctx.players.local().animation() == -1) {
				ctx.objects.select(15).id(rsc.EDGEVILLE_FURNACE).select(new Filter<GameObject>() {
					@Override
					public boolean accept(GameObject gameObject) {
						return gameObject.inViewport();
					}
				});
				if (ctx.objects.isEmpty()) {
					ctx.movement.step(furnace_tile);
					ctx.camera.turnTo(furnace_tile);
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							// TODO Auto-generated method stub
							return furnace_tile.distanceTo(ctx.players.local()) < 3;
						}
					}, 250, 10);
					break;
				}
				final Item gold = ctx.inventory.select().id(rsc.GOLD).poll();
				final GameObject furnace = ctx.objects.nearest().poll();
				final Component craft = ctx.widgets.component(rsc.WIDGET, rsc.COMPONENT_RING);

				if (!craft.visible() && furnace.interact("Smelt", furnace.name())) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							// TODO Auto-generated method stub
							return craft.visible();
						}
					}, 250, 10);
				} else if (craft.visible()) {
					lastXp = ctx.skills.experience(12);

					if (ctx.inventory.select().id(rsc.GOLD).count() >= 0) {
						if (craft.interact("Make-All")) {
							Condition.wait(new Callable<Boolean>() {

								@Override
								public Boolean call() throws Exception {
									// TODO Auto-generated method stub
									return ctx.chat.pendingInput();
								}
							}, 250, 10);
						}
					}
					Condition.sleep(Random.nextInt(200, 500));
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {

							if (ctx.inventory.select().id(rsc.GOLD).count() != 0
									&& ctx.skills.experience(12) > lastXp) {
								lastXp = ctx.skills.experience(12);
								return false;
							} else
								return true;
						}
					}, 3400, 10);
				}

			}

			break;
		case BANK:
			if (!ctx.bank.inViewport()) {
				ctx.movement.step(bank_tile);
				ctx.camera.turnTo(bank_tile);
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						// TODO Auto-generated method stub
						return bank_tile.distanceTo(ctx.players.local()) < 5;
					}
				}, 250, 10);
			} else if (ctx.bank.open()) {
				if (ctx.bank.select().id(rsc.GOLD).count(true) == 0) {
					ctx.bank.depositAllExcept(mouldId);
					log.info("No Materials");
					ctx.controller.stop();
				} else
					ctx.bank.depositAllExcept(mouldId);
				if (ctx.inventory.select().id(mouldId).count() == 0) {
					ctx.bank.withdraw(mouldId, 1);
				}
				ctx.bank.withdraw(rsc.GOLD, 27);
				ctx.bank.close();
			}

			break;
		}
	}

	private JewelerySmelter.State getState() {
		if (ctx.inventory.select().id(rsc.GOLD).count() == 0 || ctx.inventory.select().id(mouldId).count() == 0) {
			return State.BANK;
		}
		return State.CRAFT;
	}

	public void stop() {
		ctx.controller.stop();
	}

	private enum State {
		CRAFT, BANK;
	}
}
