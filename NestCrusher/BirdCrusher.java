package NestCrusher;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.*;
import javafx.*;

@Script.Manifest(name = "Dittymaster's Nest Crusher", description = "Crushes Nest for mad money", properties = "author=Fabhaz;topic=1319500;client=4;")

public class BirdCrusher extends PollingScript<ClientContext> implements PaintListener {
	private int crushedID = 6693;
	private int nestID = 5075;
	private long startTime = System.currentTimeMillis();
	private int pestleID = 233;
	private static final Font newFont = new Font("Dialog", Font.BOLD, 12);
	private String status;

	@Override
	public void repaint(Graphics graphics) {
		// TODO Auto-generated method stub
		final Graphics2D g = (Graphics2D) graphics;

		Point mouse = ctx.input.getLocation();
		g.setColor(Color.GREEN);
		g.drawLine((int) mouse.getX() - 10, (int) mouse.getY(), (int) mouse.getX() + 10, (int) mouse.getY());
		g.drawLine((int) mouse.getX(), (int) mouse.getY() - 10, (int) mouse.getX(), (int) mouse.getY() + 10);

		long runtime = System.currentTimeMillis() - startTime;

		java.lang.String hms = java.lang.String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runtime),
				TimeUnit.MILLISECONDS.toMinutes(runtime) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(runtime) % TimeUnit.MINUTES.toSeconds(1));
		g.drawString("Dittymaster's Crusher", 10, 40);
		g.drawString("Runtime: " + hms, 10, 60);
		g.drawString("Status: " + status, 10, 80);

	}

	@Override
	public void poll() {
		final State state = getState();
		if (state == null) {
			return;
		}

		switch (state) {
		case BANKING:
			status = "Banking";
			if (ctx.bank.open()) {
				if (ctx.bank.select().id(nestID).count(true) == 0) {
					ctx.bank.depositAllExcept(pestleID);
					log.info("No Nests");
					ctx.controller.stop();
				} else
					ctx.bank.depositAllExcept(pestleID);
				if (ctx.inventory.select().id(pestleID).count() == 0) {
					ctx.bank.withdraw(pestleID, 1);
				}
				ctx.bank.withdraw(nestID, 27);
				ctx.bank.close();
			}

			break;
		case CRUSHING:
			System.out.println("Curshing");
			status = "Crushing";
			if (ctx.players.local().animation() == -1) {
				final Item nest = ctx.inventory.select().id(nestID).poll();
				final Item pestle = ctx.inventory.select().id(pestleID).poll();
				if (pestle.interact("Use", pestle.name()) && nest.interact("Use", nest.name())) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							// TODO Auto-generated method stub
							return ctx.inventory.select().name("Crushed nest").count() == 27;
						}
					}, 1000, 20);
				}
			}
			break;

		}
		// TODO Auto-generated method stub

	}

	private BirdCrusher.State getState() {

		if (ctx.inventory.select().id(pestleID).count() == 0 || ctx.inventory.select().id(nestID).count() == 0) {
			return State.BANKING;
		}
		return State.CRUSHING;
	}

	public void stop() {
		ctx.controller.stop();
	}

	private enum State {
		BANKING, CRUSHING;
	}

}