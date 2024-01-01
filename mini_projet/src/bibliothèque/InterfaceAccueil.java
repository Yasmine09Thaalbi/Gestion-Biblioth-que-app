package bibliothèque;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceAccueil extends JFrame {

    public InterfaceAccueil() {
        this.setTitle("Accueil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,1000);
        setLocationRelativeTo(null);

        // Titre
        JLabel titleLabel = new JLabel("Bienvenue Dans Votre Application De Gestion de Bibliothèque");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, titleLabel.getFont().getSize() + 15));

        // Image
        ImageIcon imageIcon = new ImageIcon("C:/Users/Yasmine/eclipse-workspace/mini_projet/src/bibliothèque/biblio.png");
        Image image = imageIcon.getImage().getScaledInstance(390, 250, Image.SCALE_SMOOTH); // Adjust the width and height as needed
        ImageIcon resizedImageIcon = new ImageIcon(image);
        JLabel imageLabel = new JLabel(resizedImageIcon);
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        
        
        // Boutons
        JButton livreButton = new JButton("Gestion des Livres");
        JButton lecteurButton = new JButton("Gestion des Lecteurs");
        JButton empruntButton = new JButton("Gestion d'Emprunt et Retour");

        // Augmenter la taille des boutons et ajouter style 
        Dimension buttonSize = new Dimension(200, 70);
        livreButton.setPreferredSize(buttonSize);
        lecteurButton.setPreferredSize(buttonSize);
        empruntButton.setPreferredSize(buttonSize);
        livreButton.setFont(new Font(livreButton.getFont().getName(), Font.BOLD, livreButton.getFont().getSize() + 2));
        lecteurButton.setFont(new Font(lecteurButton.getFont().getName(), Font.BOLD, lecteurButton.getFont().getSize() + 2));
        empruntButton.setFont(new Font(empruntButton.getFont().getName(), Font.BOLD, empruntButton.getFont().getSize() + 2));

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

        // Agencement des composants avec GridBagLayout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(livreButton, gbc);

        gbc.gridx = 1;
        buttonPanel.add(lecteurButton, gbc);

        gbc.gridx = 2;
        buttonPanel.add(empruntButton, gbc);


        // Agencement principal
        JPanel mainPanel = new JPanel(new GridLayout(3, 1)); // 3 rows, 1 column

        mainPanel.add(titleLabel);
        mainPanel.add(imageLabel);
        mainPanel.add(buttonPanel);

        // Changer la couleur de fond de l'interface
        mainPanel.setBackground(new Color(204, 229, 255));

        // Agencement principal
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
