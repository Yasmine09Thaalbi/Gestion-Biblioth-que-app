package bibliothèque;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceAccueil extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InterfaceAccueil() {
        this.setTitle("Accueil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,1000);
        setLocationRelativeTo(null);

        // Titre
        JLabel titleLabel = new JLabel("Bienvenue Dans Votre Application De Gestion De Bibliothèque");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, titleLabel.getFont().getSize() + 15));

        // Image
        ImageIcon imageIcon = new ImageIcon("C:/Users/Yasmine/eclipse-workspace/mini_projet/src/bibliothèque/biblio.png");
        Image image = imageIcon.getImage().getScaledInstance(410, 260, Image.SCALE_SMOOTH); 
        ImageIcon resizedImageIcon = new ImageIcon(image);
        JLabel imageLabel = new JLabel(resizedImageIcon);
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        
        
        // Boutons
        JButton livreButton = new JButton("Gestion des Livres");
        JButton lecteurButton = new JButton("Gestion des Lecteurs");
        JButton empruntButton = new JButton("Gestion Emprunt / Retour");

        Dimension buttonSize = new Dimension(250, 70);
        livreButton.setPreferredSize(buttonSize);
        lecteurButton.setPreferredSize(buttonSize);
        empruntButton.setPreferredSize(buttonSize);
        livreButton.setFont(new Font(livreButton.getFont().getName(), Font.BOLD, livreButton.getFont().getSize() + 5));
        lecteurButton.setFont(new Font(lecteurButton.getFont().getName(), Font.BOLD, lecteurButton.getFont().getSize() + 5));
        empruntButton.setFont(new Font(empruntButton.getFont().getName(), Font.BOLD, empruntButton.getFont().getSize() + 5));

        // Actions pour les boutons
        livreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InterfaceLivre().setVisible(true);
            }
        });

        lecteurButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InterfaceLecteur().setVisible(true);
            }
        });

        empruntButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InterfaceEmpruntRetour().setVisible(true);
            }
        });

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); 

        buttonPanel.add(livreButton);
        buttonPanel.add(lecteurButton);
        buttonPanel.add(empruntButton);
        
        buttonPanel.setBorder(new EmptyBorder(50, 0, 0, 0));

        // Agencement principal
        JPanel mainPanel = new JPanel(new GridLayout(3, 1)); 

        mainPanel.add(titleLabel);
        mainPanel.add(imageLabel);
        mainPanel.add(buttonPanel);

        mainPanel.setBackground(new Color(175, 216, 245));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InterfaceAccueil().setVisible(true);
            }
        });
    }
}
