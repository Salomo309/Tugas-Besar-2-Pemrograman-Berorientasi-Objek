package UI.Page;
import javax.swing.*;

import com.itextpdf.text.DocumentException;

import DataStore.DataStore;
import Entity.Bill;
import Entity.Item;
import Entity.Member;
import UI.App;
import UI.IApp;
import Utils.Printer;
import Utils.Collections.Observer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JualBarang extends JPanel {
    private JScrollPane scrollPane;
    private JPanel stockPanel;
    private JPanel itemPanel;
    private JScrollPane stockScrollPane;
    private Bill x;
    private DataStore d;

    private int minprice = 0;
    private int maxprice = 999999999;
    private String itemName = "";

    
    private JLabel subtotalLabelNumber;

    private JComboBox<String> memberDropdown;
    private JComboBox<String> kategori;

    private String printableBill = "";
    private Printer printer = new Printer();

    public JualBarang() {
        try {
            x = new Bill(-1);
            d = DataStore.getInstance();
            d.startNewBill(x);
            
            // FILTER/SORT panel
            JPanel filterPanel = new JPanel();
            filterPanel.setLayout(null);
            filterPanel.setBackground(Color.white);
            filterPanel.setBounds(0, 0, 800, 50);
    
            // min harga
            JLabel minPriceLabel = new JLabel("Min. Price");
            minPriceLabel.setFont(new Font("Poppins", Font.BOLD, 12));
            minPriceLabel.setBounds(190, 6, 60, 40);
            
            // min harga nominal
            JTextField minPriceLabelNumber = new JTextField();
            minPriceLabelNumber.setFont(new Font("Poppins", Font.BOLD, 12));
            minPriceLabelNumber.setBounds(260, 10, 130, 30);
            minPriceLabelNumber.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inp = minPriceLabelNumber.getText();
                    try {
                        int price;
                        if (inp.equals("")) {
                            price = 0;
                        } else {
                            price = Integer.parseInt(inp);
                        }

                        minprice = price;
                        rerenderItems();
                    } catch (Exception a) {
                    }
                }
            });
            
            // max harga
            JLabel maxPriceLabel = new JLabel("Max. Price");
            maxPriceLabel.setFont(new Font("Poppins", Font.BOLD, 12));
            maxPriceLabel.setBounds(400, 6, 70, 40);
    
            // max harga nominal
            JTextField maxPriceLabelNumber = new JTextField();
            maxPriceLabelNumber.setFont(new Font("Poppins", Font.BOLD, 12));
            maxPriceLabelNumber.setBounds(470, 10, 130, 30);
            maxPriceLabelNumber.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inp = maxPriceLabelNumber.getText();
                    try {
                        int price;
                        if (inp.equals("")) {
                            price = 999999999;
                        } else {
                            price = Integer.parseInt(inp);
                        }
                        maxprice = price;
                        rerenderItems();
                    } catch (Exception a) {
                    }
                }
            });
            
            // category dropdown
            
            LinkedList <String> categories = new LinkedList<String>();
            categories.add("Category");

            for (Item x: d.getItems().getElements()) {
                if (!categories.contains(x.getCategory())) {
                    categories.add(x.getCategory());
                }
            }
            
            DefaultComboBoxModel<String> basecategoriesmodel = new DefaultComboBoxModel<>(categories.toArray(new String[0]));
            kategori = new JComboBox<>(basecategoriesmodel);
            kategori.setBounds(620, 10, 150, 30);
            kategori.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rerenderItems();
                }
                
            });
    
            // input searching
            JTextField searchField = new JTextField();
            searchField.setBounds(20, 10, 150, 30);
            searchField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inp = searchField.getText();
                    itemName = inp;
                    rerenderItems();
                }
            });
    
            filterPanel.add(searchField);
            filterPanel.add(minPriceLabel);
            filterPanel.add(minPriceLabelNumber);
            filterPanel.add(maxPriceLabel);
            filterPanel.add(maxPriceLabelNumber);
            filterPanel.add(kategori);
            
            // BILL PANEL
            // label
            JLabel billLabel = new JLabel("BILL");
            billLabel.setFont(new Font("Poppins", Font.BOLD, 24));
            billLabel.setBounds(15, 10, 50, 40);

            // checkout button
            JButton checkoutButton = new JButton("CHECKOUT");
            checkoutButton.setFocusPainted(false);
            checkoutButton.setFont(new Font("Poppins", Font.BOLD, 32));
            checkoutButton.setForeground(Color.white);
            checkoutButton.setBounds(30, 530, 329, 79);
            checkoutButton.setBackground(new Color(0x36459A));
            checkoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkoutItems();
                }
            });
    
            // member dropdown
            // String[] items = {"Pilih nama member"};

            LinkedList <String> members = new LinkedList<String>();
            members.add("Pilih nama member");

            for (Member x: d.getActiveMembers()) {
                members.add(x.getName());
            }

            DefaultComboBoxModel<String> basemembermodel = new DefaultComboBoxModel<>(members.toArray(new String[0]));
            memberDropdown = new JComboBox<>(basemembermodel);
            memberDropdown.setBounds(15, 420, 150, 30);
    
            // subtotal label
            JLabel subtotalLabel = new JLabel("Subtotal");
            subtotalLabel.setFont(new Font("Poppins", Font.BOLD, 16));
            subtotalLabel.setBounds(15, 340, 100, 40);
            
            // subtotal nominal
            subtotalLabelNumber = new JLabel("Rp " + Integer.toString(x.getTotalPrice()));
            subtotalLabelNumber.setFont(new Font("Poppins", Font.PLAIN, 16));
            subtotalLabelNumber.setBounds(230, 340, 100, 40);
    
            // diskon label
            JLabel discountLabel = new JLabel("Diskon");
            discountLabel.setFont(new Font("Poppins", Font.BOLD, 16));
            discountLabel.setBounds(15, 360, 100, 40);
    
            // diskon nominal
            JLabel discountLabelNumbers = new JLabel("RP 2.700");
            discountLabelNumbers.setFont(new Font("Poppins", Font.PLAIN, 16));
            discountLabelNumbers.setBounds(230, 360, 100, 40);
    
            // total price
            JLabel totalLabel = new JLabel("Total:");
            totalLabel.setFont(new Font("Poppins", Font.BOLD, 16));
            totalLabel.setBounds(230, 400, 70, 40);
    
            // nominal total price
            JLabel totalLabelNumbers = new JLabel("Rp 25.000");
            totalLabelNumbers.setFont(new Font("Poppins", Font.PLAIN, 16));
            totalLabelNumbers.setBounds(230, 420, 100, 40);
    
            // tombol save bill
            JButton saveBillButton = new JButton("Save Bill");
            saveBillButton.setFocusPainted(false);
            saveBillButton.setFont(new Font("Poppins", Font.BOLD, 16));
            saveBillButton.setForeground(Color.black);
            saveBillButton.setBounds(1, 470,199, 48);
            saveBillButton.setBackground(new Color(0xEBEBEB));
    
            // tombol print bill
            JButton printBillButton = new JButton("Print Bill");
            printBillButton.setFocusPainted(false);
            printBillButton.setFont(new Font("Poppins", Font.BOLD, 16));
            printBillButton.setForeground(Color.black);
            printBillButton.setBounds(199, 470,199, 48);
            printBillButton.setBackground(new Color(0xEBEBEB));
            printBillButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        printer.printComponentToPdf(printableBill);
                    } catch (DocumentException e1) {
                    }
                }
            });

            // STOCK PANEL
            stockPanel = new JPanel();
            stockPanel.setLayout(new FlowLayout());
            stockPanel.setBackground(Color.white);
            
            stockPanel = new JPanel();
            this.stockScrollPane = new JScrollPane(stockPanel);
            stockScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            stockScrollPane.setBounds(0, 50, 800, 590);
            stockScrollPane.setBorder(BorderFactory.createEmptyBorder());

            rerenderItems();
    
            // daftar belanjaan
            itemPanel = new JPanel();
            scrollPane = new JScrollPane(itemPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setBounds(10, 60, 370, 270);
            scrollPane.setBackground(Color.white);
            scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x36459A)));
            rerenderBills();
            
    
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(0x36459A)));
            panel.setBackground(Color.white);
            panel.setBounds(800, 0, 400, 620);
            panel.setLayout(null);
            panel.add(scrollPane);
            panel.add(totalLabel);
            panel.add(totalLabelNumbers);
            panel.add(subtotalLabel);
            panel.add(subtotalLabelNumber);
            panel.add(discountLabel);
            panel.add(discountLabelNumbers);
            panel.add(checkoutButton);
            panel.add(saveBillButton);
            panel.add(printBillButton);
            panel.add(billLabel);
            panel.add(memberDropdown);
    
            // frame pagenya
            // JFrame frame = new JFrame();
            this.setLayout(null);
            this.setVisible(true);
            this.setSize(1200, 720);
            this.add(filterPanel);
            this.add(panel);
            this.add(stockScrollPane);
            // frame.add(backgroundPanel);

            d.getItems().addObserver(
                new Observer() {
                    @Override
                    public void update() {
                        rerenderItems();
                        rerenderBills();
                    }   
                }
            );
            
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    public void rerenderItems() {
        this.stockPanel.removeAll();
        stockPanel = new JPanel();
        stockPanel.setLayout(new FlowLayout());
        stockPanel.setBackground(Color.white);
        
        // nanti traverse list, terus visualize satu satu
        int ctr = 0;
        
        for (Item item: d.getItems().getElements()) {
            String cat = (String) this.kategori.getSelectedItem();
            if (((!cat.equals("Category") && cat.equals(item.getCategory())) || cat.equals("Category")) && (itemName.equals("") || (!itemName.equals("") && itemName.equals(item.getName()))) && item.getPrice() <= maxprice && item.getPrice() >= minprice) {
                ctr++;
                ImageIcon image1 = new ImageIcon(getClass().getResource("/images/icon.jpg"));
                
                try {
                    BufferedImage image = ImageIO.read(new File(item.getImageUrl())); // untuk mengecek file ada atau tidak
                    image1 = new ImageIcon(item.getImageUrl());
                } catch (IOException e) {
                }
                
                Image scaledImage = image1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                image1 = new ImageIcon(scaledImage);
                JButton button1 = new JButton(image1);
                button1.setBackground(new Color(0, 0, 0, 0));
                button1.setOpaque(false);
                button1.setBorderPainted(false);
                stockPanel.add(button1);
                button1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        x.tambah(item);
                        rerenderBills();
                    }
                });   
            }
        }

        ctr = ctr / 5;            

        Dimension panelSize = new Dimension(750, (ctr+1)*116);
        stockPanel.setPreferredSize(panelSize);
        stockScrollPane.setViewportView(stockPanel);
    }

    public void rerenderBills () {
        // keperluan print
        String tab = "    ";

        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.white);

        int ctr = 0;
        printableBill = "Bill:\n\nBill ID                 : ";
        printableBill += Integer.toString(x.getId()) + "\nCustomer ID      : " + Integer.toString(x.getIdCustomer()) + "\nBarang-barang   : \n";
        for (Item item : x.getItems()) {
            ctr++;
            JPanel panelItem = new JPanel();
            panelItem.setBackground(Color.white);
            panelItem.setMaximumSize(new Dimension(1000, 90));

            JPanel itemNames = new JPanel();
            itemNames.setBounds(15, 0, 120, 20);
            itemNames.setBackground(Color.white);
            itemNames.setLayout(null);
            
            JPanel itemPrices = new JPanel();
            itemPrices.setBounds(210, 0, 120, 20);
            itemPrices.setBackground(Color.white);
            itemPrices.setLayout(null);
            
            JLabel nameLabel = new JLabel(item.getName());
            nameLabel.setHorizontalAlignment(JLabel.LEFT); // align text to left
            nameLabel.setBounds(0, 0, 120, 20);
            itemNames.add(nameLabel, BorderLayout.WEST); // add name label to left side of panel

            JLabel priceLabel = new JLabel("Rp " + item.getPrice());
            priceLabel.setHorizontalAlignment(JLabel.LEFT); // align text to right
            priceLabel.setBounds(0, 0, 120, 20);
            itemPrices.add(priceLabel, BorderLayout.EAST);

            // tombol untuk menambah/mengurangi stok
            JPanel addminPanel = new JPanel();
            addminPanel.setLayout(null);
            addminPanel.setBounds(200, 30, 130, 20);
            addminPanel.setBackground(new Color(0xD9D9D9));

            JButton min = new JButton("-");
            min.setFont(new Font("Poppins", Font.PLAIN, 10));
            min.setBackground(new Color(0xEDEDED));
            min.setLayout(null);
            min.setBounds(0, 0, 40, 20);
            min.setFocusPainted(false);
            min.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    x.hapus(item);
                    rerenderBills();
                }
            });

            JButton plus = new JButton("+");
            plus.setFont(new Font("Poppins", Font.PLAIN, 10));
            plus.setBackground(new Color(0xEDEDED));
            plus.setLayout(null);
            plus.setBounds(90, 0, 40, 20);
            plus.setFocusPainted(false);
            plus.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    x.tambah(item);
                    rerenderBills();
                }
            });

            JPanel amountPanel = new JPanel(new GridBagLayout());
            amountPanel.setBounds(40, 0, 50, 20);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel amountText = new JLabel(Integer.toString(item.getStock()));

            amountPanel.add(amountText, gbc);
            
            addminPanel.add(min);
            addminPanel.add(plus);
            addminPanel.add(amountPanel);
            
            panelItem.add(itemNames);
            panelItem.add(itemPrices);
            panelItem.add(addminPanel);
            panelItem.setLayout(null);
            itemPanel.add(panelItem);
            
            // PRINT
            printableBill += tab + "- " + item.getName() + " x" + item.getStock() + " (Rp " + item.getPrice() + ")\n";
        }
        printableBill += "Subtotal: Rp " + Integer.toString(x.getTotalPrice());
        

        Dimension billPanelSize = new Dimension(350, (ctr)*70);
        itemPanel.setPreferredSize(billPanelSize);
        scrollPane.setViewportView(itemPanel);

        subtotalLabelNumber.setText("Rp " + Integer.toString(x.getTotalPrice()));
    }

    public void checkoutItems() {
        try {
            Integer custID = d.generateCustomerId();
            String selectedMember = (String) memberDropdown.getSelectedItem();
            IApp app = App.getInstance();
            app.addTab("Transaksi Berhasil", new PembayaranBerhasil(selectedMember));
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {  
        new JualBarang();  
    } 
}   
