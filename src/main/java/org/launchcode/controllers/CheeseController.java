package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private MenuDao menuDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {
        model.addAttribute("title", "Add Cheese");
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute(new Cheese());
        return "cheese/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute  @Valid Cheese newCheese,
                                       Errors errors,
                                       @RequestParam int categoryId,
                                       Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            model.addAttribute("categories", categoryDao.findAll());
            return "cheese/add";
        }

        Category cat = categoryDao.findOne(categoryId);
        newCheese.setCategory(cat);
        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");
        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam int[] cheeseIds) {

        for (int cheeseId : cheeseIds) {
            /*
            We cannot delete a cheese if it is part of a menu, due to a foreign key constraint.
            We first need to remove that cheese from any menu that contains it and then remove it.
            */
            for (Menu menu: menuDao.findAll()) {            //we look into every menu
                List<Cheese> cheesesToRemove = new ArrayList<>();
                List<Cheese> cheeses = menu.getCheeses();   //for its list of items (cheeses)
                for (Cheese cheese: cheeses) {              //that are part of the menu.
                    if (cheese.getId()==cheeseId) {         //If the cheese we want to delete is there
                        cheesesToRemove.add(cheese);        //we add it to a list of "cheeses to be removed".
                    }
                }
                if(cheesesToRemove.size()>0){
                    cheeses.removeAll(cheesesToRemove);     //we then remove all of them at once from the list of cheeses
                    menuDao.save(menu);                     //and we update the menu.
                }
            }
            cheeseDao.delete(cheeseId);                     //finally we are ready to delete that cheese.
        }

        return "redirect:";
    }

    //BONUS MISSION 1
    @RequestMapping(value = "category/{id}", method = RequestMethod.GET)
    public String listCheesesInCategory(@PathVariable int id, Model model) {
        Category cat = categoryDao.findOne(id);

        model.addAttribute("cheeses", cat.getCheeses());
        model.addAttribute("title", "Cheeses by category: " + cat.getName());

        return "cheese/index";
    }

    //BONUS MISSION 2
    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String editCheeseForm(@PathVariable int id, Model model) {
        Cheese cheeseToEdit = cheeseDao.findOne(id);
        model.addAttribute("cheese", cheeseToEdit);
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("title", "Edit cheese");

        return "cheese/edit";
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String editCheese(@ModelAttribute @Valid Cheese cheeseEditedCopy,
                             @RequestParam int id,
                             Errors errors,
                             Model model) {

        if(errors.hasErrors()){
            model.addAttribute("title", "Edit cheese");
            return "cheese/edit";
        }

        Cheese cheese = cheeseDao.findOne(id);
        cheese.setName(cheeseEditedCopy.getName());
        cheese.setDescription(cheeseEditedCopy.getDescription());
        cheese.setCategory(cheeseEditedCopy.getCategory());
        cheeseDao.save(cheese);

        return "redirect:";
    }
}
