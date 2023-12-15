package main;

import java.awt.EventQueue;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

//view do cliente
public class View {
	Controller controller = new Controller();

	private JFrame frame;
	private JTextField contatosTextField;
	private JTextField mensagensTextfield;
	private JTextPane contatosPane;
	private JTextPane mensagensPane;
	private JButton botaoAdicionar;
	private JButton botaoDeletar;
	private JButton botaoEnviar;
	private JLabel userLabel;
	private JLabel statusLabel;
	private JButton botaoStatus;
	private JLabel lblNewLabel_2;
	private JTextField usuarioField;
	private JLabel lblNewLabel_3;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;

	/**
	 * Create the application.
	 */
	public View(String userName) {
		controller.userName = userName;
		controller.changeStatusUser();
	}

	/**
	 * Create the application.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void init() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					controller.messageConsumer = messageConsumer;

					initialize();
					frame.setVisible(true);

					controller.createUserQueueAtServer();

					userLabel.setText("Usuario: " + controller.userName);
					statusLabel.setText("Status: Online");

					String pendingMessages = getAndShowLastMessages();
					updateChat(pendingMessages);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 840, 587);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		userLabel = new JLabel("Usuario:");
		userLabel.setBounds(37, 11, 158, 14);
		frame.getContentPane().add(userLabel);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(40, 206, 264, 317);
		frame.getContentPane().add(scrollPane_1);

		contatosPane = new JTextPane();
		scrollPane_1.setViewportView(contatosPane);

		JLabel lblNewLabel = new JLabel("CONTATOS");
		lblNewLabel.setBounds(140, 182, 177, 14);
		frame.getContentPane().add(lblNewLabel);

		contatosTextField = new JTextField();
		contatosTextField.setToolTipText("digite um contato");
		contatosTextField.setBounds(37, 117, 264, 20);
		frame.getContentPane().add(contatosTextField);
		contatosTextField.setColumns(10);

		botaoAdicionar = new JButton("Adicionar");
		botaoAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.contactsList.add(contatosTextField.getText());
				contatosTextField.setText("");
				contatosPane.setText(listingContacts());
			}
		});
		botaoAdicionar.setBounds(37, 148, 129, 23);
		frame.getContentPane().add(botaoAdicionar);

		botaoDeletar = new JButton("Deletar");
		botaoDeletar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.contactsList.remove(contatosTextField.getText());
				contatosTextField.setText("");
				contatosPane.setText(listingContacts());
			}
		});
		botaoDeletar.setBounds(176, 148, 125, 23);
		frame.getContentPane().add(botaoDeletar);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(392, 153, 399, 276);
		frame.getContentPane().add(scrollPane);

		mensagensPane = new JTextPane();
		scrollPane.setViewportView(mensagensPane);

		JLabel lblNewLabel_1 = new JLabel("MENSAGENS");
		lblNewLabel_1.setBounds(561, 102, 129, 14);
		frame.getContentPane().add(lblNewLabel_1);

		mensagensTextfield = new JTextField();
		mensagensTextfield.setToolTipText("digite uma mensagem");
		mensagensTextfield.setBounds(392, 464, 399, 23);
		frame.getContentPane().add(mensagensTextfield);
		mensagensTextfield.setColumns(10);

		botaoEnviar = new JButton("Enviar");
		botaoEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String menssagem = mensagensTextfield.getText();
				String usuarioDestino = usuarioField.getText();
				try {
					controller.sendMessageToUser(usuarioDestino, menssagem);
					updateChat("Eu: " + menssagem + "\n");
				} catch (RemoteException e1) {

					e1.printStackTrace();
				}
				mensagensTextfield.setText("");

			}
		});
		botaoEnviar.setBounds(702, 500, 89, 23);
		frame.getContentPane().add(botaoEnviar);

		statusLabel = new JLabel("Status:");
		statusLabel.setBounds(37, 36, 158, 14);
		frame.getContentPane().add(statusLabel);

		botaoStatus = new JButton("mudarStatus");
		botaoStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.changeStatusUser();
				if (controller.isUserOn) {
					statusLabel.setText("Status: Online");
					String pendingMessages = getAndShowLastMessages();
					updateChat(pendingMessages);
				} else {
					statusLabel.setText("Status: Offline");
				}

			}
		});
		botaoStatus.setBounds(37, 61, 129, 20);
		frame.getContentPane().add(botaoStatus);

		lblNewLabel_2 = new JLabel("Para:");
		lblNewLabel_2.setBounds(392, 498, 46, 20);
		frame.getContentPane().add(lblNewLabel_2);

		usuarioField = new JTextField();
		usuarioField.setText("");
		usuarioField.setBounds(425, 498, 153, 20);
		frame.getContentPane().add(usuarioField);
		usuarioField.setColumns(10);

		lblNewLabel_3 = new JLabel("Mensagem:");
		lblNewLabel_3.setBounds(392, 440, 104, 14);
		frame.getContentPane().add(lblNewLabel_3);
	}

	public String listingContacts() {
		StringBuilder contactsNames = new StringBuilder();
		controller.contactsList.forEach(contact -> {
			contactsNames.append("CONTATO : " + contact + ". \n");
		});
		return contactsNames.toString();
	}

	public String getAndShowLastMessages() {
		List<String> messages = controller.getUserMessagesAtServer();
		StringBuilder messageBuilder = new StringBuilder();
		messages.forEach(message -> {
			messageBuilder.append(message + ". \n");
		});
		return messageBuilder.toString();
	}

	void updateChat(String Text) {
		String previusText = mensagensPane.getText();
		if (previusText == null) {
			previusText = "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(previusText);
		stringBuilder.append(Text);
		String newText = stringBuilder.toString();
		mensagensPane.setText(newText);

	}

	Consumer<String> messageConsumer = (message) -> {
		String previusText = mensagensPane.getText();
		if (previusText == null) {
			previusText = "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(previusText);
		stringBuilder.append(message);
		String newText = stringBuilder.toString();
		mensagensPane.setText(newText);

	};
	

}
