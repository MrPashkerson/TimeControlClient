package client.mvc;

public final class SingletonModel {
    private static volatile SingletonModel instance;

    public static Model model;
    public static String isConnected;
    public static String searchType;
    public static String formType;
    public static String editUser;

    private SingletonModel() {
        this.model = new Model();
        this.isConnected = null;
        this.searchType = "";
        this.formType = "";
        this.editUser = "";
    }

    public static String getIsConnected() {
        return isConnected;
    }

    public static void setIsConnected(String isConnected) {
        SingletonModel.isConnected = isConnected;
    }

    public static String getSearchType() {
        return searchType;
    }

    public static void setSearchType(String searchType) {
        SingletonModel.searchType = searchType;
    }

    public static String getFormType() {
        return formType;
    }

    public static void setFormType(String formType) {
        SingletonModel.formType = formType;
    }

    public static String getEditUser() {
        return editUser;
    }

    public static void setEditUser(String editUser) {
        SingletonModel.editUser = editUser;
    }

    public static SingletonModel getInstance() {
        SingletonModel result = instance;
        if (result != null) {
            return result;
        }
        synchronized(SingletonModel.class) {
            if (instance == null) {
                instance = new SingletonModel();
            }
            return instance;
        }
    }
}
