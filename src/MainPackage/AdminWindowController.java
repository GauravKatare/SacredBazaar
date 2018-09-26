package MainPackage;

import AdminQueries.ChangeProduct;
import AdminQueries.Customershow;
import AdminQueries.LoadAdminDetails;
import AdminQueries.RemoveProduct;
import CustomerQueries.ChangeCustomerDetails;
import CustomerQueries.LoadCustomerDetails;
import CustomerQueries.SearchFor;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class AdminWindowController
{

    public BorderPane AdminPane;
    public VBox LeftVBox;
    public VBox TopVBox;
    public TextArea SearchBar;
    FXMLLoader loader;
    public ScrollPane CentreDisplay;
    private Admin admin;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private VBox vBox;
    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void showcustomer() throws IOException, ClassNotFoundException
    {
        System.out.println("Giving List of customers");
        Customershow cs=new Customershow();
        oos.writeObject(cs);
        oos.flush();
        ArrayList<Customer> show=(ArrayList<Customer>) ois.readObject();
        ShowProductList(show);
    }

    private void ShowProductList(ArrayList<Customer>customerList) throws IOException
    {
        VBox vBox = new VBox();
        int len=customerList.size();
        customeradmincontroller controller;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customerdisplayadmin.fxml")) ;
        HBox customerDetailsDisplay = new HBox();
        customerDetailsDisplay.getChildren().add(new Label("Rank"));
        customerDetailsDisplay.getChildren().add(new Label("Customer UserName"));
        customerDetailsDisplay.getChildren().add(new Label("Total Spending"));
        for(int i=0;i<len;i++)
        {
            customerDetailsDisplay = (HBox) loader.load();
            controller = loader.getController();
            Customer con = customerList.get(i);
            controller.customerno.setText(""+(i+1)+"");
            controller.UserName.setText(con.getUserName());
            controller.customerspending.setText("Rs. "+con.getTotalspending());
            vBox.getChildren().add(customerDetailsDisplay);
        }
        AdminPane.setCenter(vBox);
    }

    public void SearchProduct()
    {
        SearchFor sf = new SearchFor();
        sf.searchProduct=SearchBar.getText();
        ArrayList<Product> prodList = null;
        try {
            oos.writeObject(sf);
            oos.flush();
            prodList = (ArrayList<Product>) ois.readObject();
            showProductList(prodList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showProductList(ArrayList<Product> prodList)
    {
        VBox displayForProducts = new VBox();
        for (int i = 0; i < prodList.size(); i++)
        {
            VBox vBox = null;
            loader = new FXMLLoader(getClass().getResource("ProductInfo.fxml"));
            ProductInfoController controller;
            try {
                vBox = loader.load();
                controller = loader.getController();
                controller.CategoryBox.setText(prodList.get(i).getProductCategory());
                controller.ProductIdBox.setText(Integer.toString(prodList.get(i).getProductId()));
                controller.DescriptionBox.setText(prodList.get(i).getProductDescription());
                controller.DiscountBox.setText(Integer.toString(prodList.get(i).getDiscount()));
                controller.PriceBox.setText(Integer.toString(prodList.get(i).getPrice()));
                controller.QuantityBox.setText(Integer.toString(prodList.get(i).getQuantity()));
                controller.RetailerUserNameBox.setText(prodList.get(i).getRetailer());
                Product p = prodList.get(i);
                controller.DeleteProduct.setOnAction(e -> RemoveProduct(p));
                controller.ProductChanges.setOnAction(e ->
                {
                    p.setProductCategory((controller.CategoryBox.getText()));
                    p.setPrice(Integer.parseInt(controller.PriceBox.getText()));
                    p.setDiscount(Integer.parseInt(controller.DiscountBox.getText()));
                    p.setQuantity(Integer.parseInt(controller.QuantityBox.getText()));
                    p.setProductDescription(controller.DescriptionBox.getText());
                    p.setRetailer(controller.RetailerUserNameBox.getText());
                    SaveChanges(p);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            displayForProducts.getChildren().add(vBox);
        }
        AdminPane.setCenter(displayForProducts);
    }

    private void RemoveProduct(Product p)
    {
        RemoveProduct rp = new RemoveProduct();
        rp.ProductId = p.getProductId();
        try {
            oos.writeObject(rp);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void SaveChanges(Product prod)
    {
        ChangeProduct cp = new ChangeProduct();
        cp.product = prod;
        try {
            oos.writeObject(cp);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void ShowProfile() throws IOException, ClassNotFoundException {
        LoadAdminDetails lad = new LoadAdminDetails();
        lad.UserName=admin.getUserName();
        oos.writeObject(lad);
        oos.flush();
        admin = (Admin) ois.readObject();
        loader = new FXMLLoader(getClass().getResource("AdminProfile.fxml")) ;
        AdminProfileController controller;
        try
        {
            CentreDisplay= (ScrollPane) loader.load();
            controller = loader.getController();
            controller.admin =  admin;
            controller.oos = oos;
            controller.ois = ois;
            controller.ShowProfile();
            AdminPane.setCenter(CentreDisplay);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void getUserInfo()
    {
        TextField tf = new TextField();
        tf.setPromptText("Search User");
        Button btn = new Button();
        btn.setOnAction(e-> {
            try {
                displayInfo(tf.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        vBox = new VBox();
        HBox hBox = new HBox();
        hBox.getChildren().addAll(tf,btn);
        vBox.getChildren().add(hBox);
        AdminPane.setCenter(vBox);
    }
    public void displayInfo(String UserName) throws IOException, ClassNotFoundException {
        vBox.getChildren().clear();
        LoadCustomerDetails lcd = new LoadCustomerDetails();
        lcd.userName = UserName;
        oos.writeObject(lcd);
        oos.flush();
        Customer customer = (Customer)ois.readObject();
        Label UserNameLabel,PasswordLabel,EmailLabel,MobileNoLabel,FirstNameLabel,LasNameLabel,AddressLabel,PinNoLabel;
        TextArea UserNameArea,PasswordArea,FirstNameArea,LastNameArea,MobileNoArea,AddressArea,EmailArea,PinNoArea;
        Button SaveProfile;
        UserNameLabel = new Label("UserName");
        PasswordLabel = new Label("Password");
        UserNameArea = new TextArea();
        UserNameArea.setText(customer.getUserName());
        UserNameArea.setDisable(true);
        PasswordArea = new TextArea();
        PasswordArea.setText(customer.getPassword());
        EmailLabel = new Label("Email");
        MobileNoLabel = new Label("Mobile No");
        FirstNameLabel = new Label("First Name");
        LasNameLabel = new Label("Last Name");
        AddressLabel = new Label("Address");
        PinNoLabel = new Label("Pin No");
        PinNoArea = new TextArea();
        PinNoArea.setText(customer.getPinNo());
        FirstNameArea = new TextArea();
        FirstNameArea.setText(customer.getFirstName());
        EmailArea = new TextArea();
        EmailArea.setText(customer.getEmail());
        LastNameArea = new TextArea();
        LastNameArea.setText(customer.getLastName());
        MobileNoArea = new TextArea();
        MobileNoArea.setText(customer.getMobileNo());
        AddressArea = new TextArea();
        AddressArea.setText(customer.getAddress());
        SaveProfile = new Button("Save Your Changes");
        SaveProfile.setOnAction(e -> {
            try {
                ChangeCustomerDetails ccd = new ChangeCustomerDetails();
                Customer c = new Customer(FirstNameArea.getText(),LastNameArea.getText(),UserNameArea.getText(),PasswordArea.getText(),AddressArea.getText(),MobileNoArea.getText(),PinNoArea.getText(),EmailArea.getText(),null,null,null);
                ccd.client = c;
                oos.writeObject(ccd);
                oos.flush();
                c = (Customer)ois.readObject();
                displayInfo(UserName);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        vBox.getChildren().addAll(UserNameLabel, UserNameArea, PasswordLabel, PasswordArea, FirstNameLabel, FirstNameArea, LasNameLabel, LastNameArea, EmailLabel, EmailArea, MobileNoLabel, MobileNoArea, AddressLabel, AddressArea,PinNoLabel,PinNoArea,SaveProfile);
    }
}
