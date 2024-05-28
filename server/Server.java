import java.rmi.server.UnicastRemoteObject; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static Product[] loadProductsFromXML(String filePath) throws Exception {
        List<Product> products = new ArrayList<>();
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("product");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                double price = Double.parseDouble(eElement.getElementsByTagName("price").item(0).getTextContent());

                products.add(new ProductImp(name, description, price));
            }
        }
        return products.toArray(new Product[0]);
    }

    public static void viewProducts(Registry registry) throws Exception { 
        Product[] products = loadProductsFromXML("products.xml");

        for (Product product : products) {
            System.out.println("Name: " + product.Getname());
            System.out.println("Price: " + product.Getprice());
            System.out.println("Description: " + product.Getdesc());
            System.out.println("Hello?  ");
        }
    }

    public static void main(String[] args) {
        try {
            Registry startRMI = LocateRegistry.createRegistry(9200); 
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            System.out.println("The server is running");

            Product[] products = loadProductsFromXML("products.xml");

            for (Product product : products) {
                Product stub = (Product) UnicastRemoteObject.exportObject(product, 0);
                Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9200);
                registry.bind(product.Getname(), stub);
            }

            Cart cart = new CartImp();
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9200);
            registry.bind("Cart", cart);

            viewProducts(registry);
            System.out.println("Exporting and Binding done...");

        } catch (Exception e) {
            System.out.println("Some server error ..." + e);
        }
    }

}
