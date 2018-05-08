package gr.uom.pam.model;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public static List<Category> GetCategories(){
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("MULTI", "MULTI"));
        categories.add(new Category("NDG", "NDG"));
        categories.add(new Category("NESTLE PROFESSIONAL", "NESTLE PROFESSIONAL"));
        categories.add(new Category("ΔΗΜΗΤΡΙΑΚΑ", "ΔΗΜΗΤΡΙΑΚΑ"));
        categories.add(new Category("ΕΛΛΗΝΙΚΟΣ ΚΑΦΕΣ", "ΕΛΛΗΝΙΚΟΣ ΚΑΦΕΣ"));
        categories.add(new Category("ΖΩΟΤΡΟΦΕΣ", "ΖΩΟΤΡΟΦΕΣ"));
        categories.add(new Category("ΜΑΓΕΙΡΙΚΑ", "ΜΑΓΕΙΡΙΚΑ"));
        categories.add(new Category("ΝΕΡΑ", "ΝΕΡΑ"));
        categories.add(new Category("ΠΑΙΔΙΚΕΣ ΤΡΟΦΕΣ", "ΠΑΙΔΙΚΕΣ ΤΡΟΦΕΣ"));
        categories.add(new Category("ΡΟΦΗΜΑΤΑ", "ΡΟΦΗΜΑΤΑ"));
        categories.add(new Category("ΣΟΚΟΛΑΤΕΣ", "ΣΟΚΟΛΑΤΕΣ"));
        categories.add(new Category("ΣΤΙΓΜΙΑΙΟΙ ΚΑΦΕΔΕΣ", "ΣΤΙΓΜΙΑΙΟΙ ΚΑΦΕΔΕΣ"));
        return categories;
    }

    public static List<Store> GetStores() {
        List<Store> stores = new ArrayList<>();
        stores.add(new Store("ΑΒ", "AB"));
        stores.add(new Store("MARKET IN", "BGs\\MARKET IN"));
        stores.add(new Store("ΑΝΕΔΗΚ ΚΡΗΤΙΚΟΣ", "ELOMAS\\ΑΝΕΔΗΚ ΚΡΗΤΙΚΟΣ"));
        stores.add(new Store("ΠΡΟΜΗΘΕΥΤΙΚΗ)", "ELOMAS\\ΠΡΟΜΗΘΕΥΤΙΚΗ"));
        stores.add(new Store("ΕΝΑ C&C", "ΕΝΑ C&C"));
        stores.add(new Store("LIDL", "LIDL"));
        stores.add(new Store("MAKRO", "MART)"));
        stores.add(new Store("MASOUTIS", "MASOUTIS"));
        stores.add(new Store("MASOUTIS C&C", "MASOUTIS C&C"));
        stores.add(new Store("METRO C&C", "METRO C&C"));
        stores.add(new Store("MY MARKET", "MY MARKET"));
        stores.add(new Store("Γαλαξίας", "PENTE"));
        stores.add(new Store("E.Y.Σ.", "SKLAVENITIS "));
        stores.add(new Store("OK STORES", "OK STORES"));
        return stores;
    }

}
