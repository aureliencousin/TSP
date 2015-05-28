package ants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class MainWindow extends JFrame implements ActionListener {
	

	private static final long serialVersionUID = 1L;

	private JPanel container = new JPanel();

	Dimension fieldSize = new Dimension(40,30);
    JTextField upbsize = new JTextField("10");
    JTextField lbsize = new JTextField("5");
    JTextField upphero = new JTextField("0");
    JTextField lophero = new JTextField("0");
    JTextField addants = new JTextField("30");
    JTextField nstep = new JTextField("5");
    JTextField evap = new JTextField("3");
	JButton reset = new JButton("Reset");
	JButton nextstep = new JButton("Next Step");
	
	
	private Simulation sim;

	public MainWindow (Simulation s) {
	    sim = s;		
		this.setTitle("Complex systems: Ants");
		this.setSize(this.getToolkit().getScreenSize());
		
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBackground(Color.GRAY);
		this.setResizable(false);

	    initComponents();
	    this.setContentPane(container);
	    this.setVisible(true);
	    

	    System.out.println(sim);
	}

	private void initComponents() {

		JPanel options = new JPanel();
		options.setPreferredSize(new Dimension(this.getWidth(), 50));
		FlowLayout fl= new FlowLayout(FlowLayout.LEFT,10,10);
		options.setLayout(fl);
		options.add(new JLabel("Upper path size"));
		options.add(upbsize);
		options.add(new JLabel("t0 Pheromones"));
		options.add(upphero);
		options.add(new JLabel("Lower path size"));
		options.add(lbsize);
		options.add(new JLabel("t0 Pheromones"));
		options.add(lophero);
		options.add(new JLabel("| Add"));
		options.add(addants);
		options.add(new JLabel("ants every"));
		options.add(nstep);
		options.add(new JLabel("steps. Evaporation:"));
		options.add(evap);
		options.add(reset);
		options.add(nextstep);
		reset.addActionListener(this);
		nextstep.addActionListener(this);
		sim.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()-50));
		upbsize.setPreferredSize(fieldSize);
		lbsize.setPreferredSize(fieldSize);
		upphero.setPreferredSize(fieldSize);
		lophero.setPreferredSize(fieldSize);
		addants.setPreferredSize(fieldSize);
		nstep.setPreferredSize(fieldSize);
		evap.setPreferredSize(fieldSize);
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		container.add(options);
		container.add(sim);
	}

	public static void main(String args[]) {
		
		Simulation sim = new Simulation (5,8);
		new MainWindow(sim);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.reset) {
			sim.UPBSIZE = Integer.parseInt(this.upbsize.getText());
			sim.LBSIZE = Integer.parseInt(this.lbsize.getText());
			sim.init();
			int evapo = Integer.parseInt(evap.getText())+1;
			sim.setEvaporation(evapo);
			sim.upperb.addPheromones(Integer.parseInt(this.upphero.getText()),evapo);
			sim.lowerb.addPheromones(Integer.parseInt(this.lophero.getText()),evapo);
			sim.setNewAnts(Integer.parseInt(this.addants.getText()));
			int nstep = Integer.parseInt(this.nstep.getText());
			if(nstep <= 0) {
				nstep = 1;
				this.nstep.setText("1");
			}
				
			sim.setNstep(nstep);
			System.out.println("reset");
		}
		if(e.getSource() == this.nextstep) {
			sim.step();
		}
		
	}
}
