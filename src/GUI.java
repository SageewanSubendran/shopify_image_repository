package shopify_image_repository;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI implements ActionListener{

	private int orders = 0;
	private JLabel label;
	private JFrame frame;
	private JPanel panel;
	
	public GUI() {
		frame = new JFrame();
		
		//Button Setup
		JButton button = new JButton("Buy Now");
		button.addActionListener(this);
		label = new JLabel("Number of Orders: 0");
		
		//Panel Setup
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(200, 200, 80, 200));
		panel.setLayout(new GridLayout(0, 1));
		panel.add(button);
		panel.add(label);
		
		//Set the Panel to the frame, continue setup
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Shopify Image Shop");
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new GUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		orders++;
		label.setText("Number of orders: " + orders);
	}

}
