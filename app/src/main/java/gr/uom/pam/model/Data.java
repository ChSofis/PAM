package gr.uom.pam.model;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public static List<Category> GetCategories(){
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("MULTI", "MULTI"));
        categories.add(new Category("Μαγειρικά", "ΜΑΓΕΙΡΙΚΑ"));
        categories.add(new Category("Σοκολάτα", "ΣΟΚΟΛΑΤΕΣ"));
        categories.add(new Category("CPW", "ΔΗΜΗΤΡΙΑΚΑ"));
        categories.add(new Category("Purina", "ΖΩΟΤΡΟΦΕΣ"));
        categories.add(new Category("Nestle Waters", "ΝΕΡΑ"));
        categories.add(new Category("Ελληνικός Καφές", "ΕΛΛΗΝΙΚΟΣ ΚΑΦΕΣ"));
        categories.add(new Category("Στιγμιαίος Καφές", "ΣΤΙΓΜΙΑΙΟΙ ΚΑΦΕΔΕΣ"));
        categories.add(new Category("Nestle Professional - OOH", "NESTLE PROFESSIONAL"));
        categories.add(new Category("Dolce Gusto", "NDG"));
        categories.add(new Category("Ροφήματα", "ΡΟΦΗΜΑΤΑ"));
        categories.add(new Category("Παιδική Διατροφή", "ΠΑΙΔΙΚΕΣ ΤΡΟΦΕΣ"));
        return categories;
    }

    public static List<Store> GetStores() {
        List<Store> stores = new ArrayList<>();
        stores.add(new Store("ΑΒ Βασιλόπουλος", "AB"));
        stores.add(new Store("E.Y.Σ.", "SKLAVENITIS"));
        stores.add(new Store("My Market", "MY MARKET"));
        stores.add(new Store("Γαλαξίας", "PENTE"));
        stores.add(new Store("Market In", "BGs/1.MARKET IN"));
        stores.add(new Store("ΑΝΕΔΗΚ Κρητικός", "ELOMAS/1. ΑΝΕΔΗΚ ΚΡΗΤΙΚΟΣ") );
        stores.add(new Store("ΠΡΟΜΗΘΕΥΤΙΚΗ", "ELOMAS/3. ΠΡΟΜΗΘΕΥΤΙΚΗ"));
        stores.add(new Store("Μασούτης", "MASOUTIS"));
        stores.add(new Store("Ok Anytime Stores", "ΟΚ STORES"));
        stores.add(new Store("LIDL", "LIDL"));
        stores.add(new Store("ΕΝΑ C&C", "ENA C&C"));
        stores.add(new Store("MART", "MAKRO)"));
        stores.add(new Store("Μασούτης C&C", "MASOUTIS C&C"));
        stores.add(new Store("METRO C&C", "METRO C&C"));

        return stores;
    }

}
