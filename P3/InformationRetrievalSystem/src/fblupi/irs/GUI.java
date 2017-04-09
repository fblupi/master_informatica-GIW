package fblupi.irs;

import news.New;
import org.apache.lucene.store.FSDirectory;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;
import java.util.List;

import static org.apache.lucene.index.DirectoryReader.indexExists;

/**
 * Search Engine GUI
 *
 * @author fblupi
 */
public class GUI {
    private JButton importIndexerButton;
    private JTextField inputQuery;
    private JButton searchButton;
    private JList newsList;
    private JTextArea newText;
    private JScrollPane newsPane;
    private JScrollPane newPane;
    private JLabel numberOfResultsLabel;
    private JPanel panelMain;

    private String indexDirectoryName;
    private String stopWordsFileName = "data/palabras_vacias_utf8.txt";

    private List<New> news;

    private void importIndex() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Index folder selection");

            int returnVal = fileChooser.showOpenDialog(panelMain);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String directory = fileChooser.getSelectedFile().getPath();

                if (indexExists(FSDirectory.open(new File(directory)))) {
                    JOptionPane.showMessageDialog(panelMain, "Index in the folder:\n" + directory + "\nhas been successfully loaded", "Index successfully loaded", JOptionPane.INFORMATION_MESSAGE);

                    indexDirectoryName = directory;

                    inputQuery.setEnabled(true);
                    searchButton.setEnabled(true);
                    importIndexerButton.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(panelMain, "There where an error while loading index in the folder:\n" + directory, "Error loading index", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void search() {
        try {
            if (inputQuery.getText().equals("")) {
                JOptionPane.showMessageDialog(panelMain, "You have written no query", "Error searching" , JOptionPane.ERROR_MESSAGE);
            } else {
                long timeIni, timeEnd;

                DefaultListModel list = new DefaultListModel();

                timeIni = System.currentTimeMillis();
                news = SearchEngine.search(indexDirectoryName, stopWordsFileName, inputQuery.getText());
                timeEnd = System.currentTimeMillis();

                for (int i = 0; i < news.size(); i++) {
                    list.add(i, news.get(i).getTitle());
                }

                newsList.setModel(list);
                numberOfResultsLabel.setText(news.size() + " results were found in " + (double)(timeEnd - timeIni) / 1000 + " seconds");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void selectNew() {
        int i = newsList.getSelectedIndex();
        newText.setText(news.get(i).getText());
    }

    public GUI() {
        importIndexerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                importIndex();
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                search();
            }
        });
        inputQuery.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    search();
                } else {
                    super.keyPressed(keyEvent);
                }
            }
        });
        newsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    selectNew();
                }
                super.mousePressed(mouseEvent);
            }
        });
        newsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectNew();
                } else {
                    super.keyPressed(keyEvent);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Searchator");
        frame.setContentPane(new GUI().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
