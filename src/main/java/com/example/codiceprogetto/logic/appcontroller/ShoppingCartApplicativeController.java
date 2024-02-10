package com.example.codiceprogetto.logic.appcontroller;

import com.example.codiceprogetto.logic.bean.CartPriceBean;
import com.example.codiceprogetto.logic.dao.CartDAO;
import com.example.codiceprogetto.logic.entities.Product;
import com.example.codiceprogetto.logic.exception.DAOException;
import com.example.codiceprogetto.logic.utils.SessionUser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartApplicativeController {
    public CartPriceBean calculatePrice(CartPriceBean price) throws SQLException, DAOException {
        double amount;

        amount = new CartDAO().retrieveCartTotal(SessionUser.getInstance().getThisUser().getEmail());

        price.setTotal(amount);
        price.setTax((amount/100)*22);
        price.setSubtotal(price.getTotal() - price.getTax());

        return price;
    }

    public List<String> retrieveCartProd() throws DAOException, SQLException{
        List<Product> cartContent;
        List<String> prodNameList = new ArrayList<>();

        cartContent = new CartDAO().retrieveCartContent(SessionUser.getInstance().getThisUser().getEmail());
        if (cartContent == null)
            return null;

        for(Product prod : cartContent) {
            prodNameList.add(prod.getName());
        }

        return prodNameList;
    }
}
