package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {
    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Cheese menu");
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(@ModelAttribute @Valid Menu menu,
                      Errors errors,
                      Model model) {
        if(errors.hasErrors()){
            //model.addAttribute(menu);
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int id, Model model){
        Menu menu = menuDao.findOne(id);
        if(menu == null) {
            return "redirect:/menu";
        }
        model.addAttribute("title", "Menu: " + menu.getName());
        model.addAttribute("menu", menu);

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(@PathVariable int id, Model model){
        Menu menu = menuDao.findOne(id);
        if(menu == null) {
            return "redirect:/menu";
        }

        AddMenuItemForm menuForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("form", menuForm);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm menuForm,
                          Errors errors,
                          Model model){
        if(errors.hasErrors()){
            model.addAttribute("title", "Add item to menu: " + menuForm.getMenu().getName());
            return "menu/add-item";
        }

        Menu menu = menuDao.findOne(menuForm.getMenuId());
        Cheese cheese = cheeseDao.findOne(menuForm.getCheeseId());
        if((menu == null)||(cheese == null)) {
            return "redirect:/menu";
        }
        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:";
    }

}
