package RetailerQueries;

import MainPackage.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoadSoldProducts
{
    public Connection connection;
    public String UserName;

    public LoadSoldProducts(Connection connection, String userName)
    {
        this.connection = connection;
        this.UserName=userName;
    }

    public ArrayList<Product> loadProducts() throws SQLException
    {
        String query = "SELECT * FROM TransactionTable ";
        ArrayList<Product> prodList = new ArrayList<>();
        PreparedStatement prepStat = connection.prepareStatement(query);
        ResultSet rs = prepStat.executeQuery();
        while(rs.next())
        {
            String q = "SELECT * FROM ProductsTable WHERE ProductId='" + (rs.getString("ProductId")) + "'";
            PreparedStatement ps = connection.prepareStatement(q);
            ResultSet res = ps.executeQuery();
            res.next();
            Product prod = new Product(res.getString("ProductId"),res.getString("Retailer"),res.getInt("Price"),rs.getInt("Quantity"),res.getString("Category"),res.getString("Description"),res.getInt("Discount"));
            prodList.add(prod);
        }
        return prodList;
    }

}
