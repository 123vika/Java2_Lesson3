package multusession_1;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientFrame extends JFrame {
    // адрес сервера
    private static String SERVER_HOST = "localhost";
    // порт
    private static int SERVER_PORT = 8080;
    // клиентский сокет
    private Socket clientSocket;
    // входящее сообщение
    private Scanner inMessage;
    // исходящее сообщение
    private PrintWriter outMessage;
    // следующие поля отвечают за элементы формы
    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;
    // имя клиента
    private String clientName = "";
    // получаем имя клиента
    public String getClientName() {
        return this.clientName;
    }

    // конструктор
    public ClientFrame() {
        JFrame jFrame = new JFrame();
        JPanel panel = new JPanel(new GridLayout(5, 1));
        JLabel l1 = new JLabel("Введите адрес сервера");
        JLabel l2 = new JLabel("Введите номер порта");
        JButton button = new JButton("OK");
        JTextArea host = new JTextArea();
        JTextArea port = new JTextArea();
        panel.add(l1);
        panel.add(host);
        panel.add(l2);
        panel.add(port);
        panel.add(button);

        jFrame.add(panel);
        jFrame.setBounds(0, 0, 500, 300);
        jFrame.setVisible(true);
        button.addActionListener(e->{
            SERVER_HOST = host.getText();
            SERVER_PORT = Integer.parseInt(port.getText());
            port.setText("Порт и хост введены, можно зкрыть окно");
            try {
                // подключаемся к серверу
                clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
                inMessage = new Scanner(clientSocket.getInputStream());
                outMessage = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // Задаём настройки элементов на форме
            setBounds(600, 300, 600, 500);
            setTitle("Client");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jtaTextAreaMessage = new JTextArea();
            jtaTextAreaMessage.setEditable(false);
            jtaTextAreaMessage.setLineWrap(true);
            JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
            add(jsp, BorderLayout.CENTER);
            // label, который будет отражать количество клиентов в чате
            JLabel jlNumberOfClients = new JLabel("Количество клиентов в чате: ");
            add(jlNumberOfClients, BorderLayout.NORTH);
            JPanel bottomPanel = new JPanel(new BorderLayout());
            add(bottomPanel, BorderLayout.SOUTH);
            JButton jbSendMessage = new JButton("Отправить");
            bottomPanel.add(jbSendMessage, BorderLayout.EAST);
            jtfMessage = new JTextField("Введите ваше сообщение: ");
            bottomPanel.add(jtfMessage, BorderLayout.CENTER);
            jtfName = new JTextField("Введите ваше имя: ");
            bottomPanel.add(jtfName, BorderLayout.WEST);
            // обработчик события нажатия кнопки отправки сообщения
            jbSendMessage.addActionListener(event -> {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    // фокус на текстовое поле с сообщением
                    jtfMessage.grabFocus();
                }
            });
            // при фокусе поле сообщения очищается
            jtfMessage.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    jtfMessage.setText("");
                }
            });
            // при фокусе поле имя очищается
            jtfName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    jtfName.setText("");
                }
            });
            // в отдельном потоке начинаем работу с сервером
            new Thread(() -> {
                try {
                    // бесконечный цикл
                    while (true) {
                        // если есть входящее сообщение
                        if (inMessage.hasNext()) {
                            // считываем его
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Клиентов в чате = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                // выводим сообщение
                                jtaTextAreaMessage.append(inMes);
                                // добавляем строку перехода
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e1) {
                }
            }).start();
            // добавляем обработчик события закрытия окна клиентского приложения
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    try {
                        // здесь проверяем, что имя клиента непустое и не равно значению по умолчанию
                        if (!clientName.isEmpty() && !clientName.equals("Введите ваше имя: ")) {
                            outMessage.println(clientName + " вышел из чата!");
                        } else {
                            outMessage.println("Участник вышел из чата, так и не представившись!");
                        }
                        // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
                        outMessage.println("##session##end##");
                        outMessage.flush();
                        outMessage.close();
                        inMessage.close();
                        clientSocket.close();
                    } catch (IOException exc) {

                    }
                }
            });
            // отображаем форму
            setVisible(true);
        });

    }

    // отправка сообщения
    public void sendMsg() {
        // формируем сообщение для отправки на сервер
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        // отправляем сообщение
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }
}