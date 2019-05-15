package AutoEnchanter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class GUI extends JFrame {
	private JPanel p = new JPanel();
	private JButton startButton = new JButton("Start");
	private JButton cancelButton = new JButton("Cancel");
	private Resources rsc;
	String gem;
	String jewelery;
	boolean valid = false;

	GUI(final AutoEnchant instance) {
		setSize(400, 100);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Auto Enchanter");
		rsc = new Resources();
		final JComboBox gemComboBox = new JComboBox(rsc.gemEnchant);
		final JComboBox jeweleryCombBox = new JComboBox(rsc.jeweleryType);

		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				jewelery = rsc.jeweleryType[jeweleryCombBox.getSelectedIndex()];
				gem = rsc.gemEnchant[gemComboBox.getSelectedIndex()];
				valid = true;
				dispose();

			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				valid = true;
				instance.stop();
				dispose();

			}
		});

		setLayout(new BorderLayout());
		p.add(new JLabel("Gem:"));
		p.add(gemComboBox);
		p.add(new JLabel("Type:"));
		p.add(jeweleryCombBox);
		p.add(startButton);
		p.add(cancelButton);
		add(p);
	}
}
