import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Main extends JFrame {

    public Main(){
        super("title");
        setSize(420, 420);
        JButton button1 = new JButton();
        JButton button2 = new JButton();
        setLayout(null);
        JTextField textField = new JTextField(20);
        textField.setText("псевдо калькулятор");
        textField.setBounds(0,0, 400, 20);
        button1.setBounds(100, 100, 100, 20);
        button2.setBounds(100, 200, 100, 20);
        add(button1);
        add(button2);
        add(textField);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("1");
            }

        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("2");
            }

        });
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }
}