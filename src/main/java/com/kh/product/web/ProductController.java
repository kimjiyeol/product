package com.kh.product.web;

import com.kh.product.domain.dao.entity.Product;
import com.kh.product.domain.svc.ProductSVC;
import com.kh.product.web.form.product.AllForm;
import com.kh.product.web.form.product.DetailForm;
import com.kh.product.web.form.product.SaveForm;
import com.kh.product.web.form.product.UpdateForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductSVC productSVC;

  @GetMapping("/add")
  public String addForm(Model model) {
    log.info("addForm 호출됨!");
    model.addAttribute("saveForm", new SaveForm());
    return "product/add";
  }

  @PostMapping("/add")
  public String add(@Valid @ModelAttribute SaveForm saveForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    log.info("add 호출됨!={}",saveForm);

    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/add";
    }

    if(saveForm.getQuantity() > 1000) {
      bindingResult.rejectValue("quantity","product",new Object[]{1000},null);
    }

    if(saveForm.getQuantity() * saveForm.getPrice() > 100_000_000L) {
      bindingResult.reject("totalPrice",new Object[]{1000},null);
    }

    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/add";
    }

    Product product = new Product();
    product.setPname(saveForm.getPname());
    product.setQuantity(saveForm.getQuantity());
    product.setPrice(saveForm.getPrice());
    Long pid = productSVC.save(product);

    log.info("상품아이디={}",pid);
    redirectAttributes.addAttribute("id", pid);

    return "redirect:/products/{id}/detail";
  }

  @GetMapping("/{id}/detail")
  public String findById(@PathVariable("id") Long id, Model model) {
    Optional<Product> findedProduct = productSVC.findById(id);
    Product product = findedProduct.orElseThrow();

    DetailForm detailForm = new DetailForm();
    detailForm.setPid(product.getPid());
    detailForm.setPname(product.getPname());
    detailForm.setQuantity(product.getQuantity());
    detailForm.setPrice(product.getPrice());

    model.addAttribute("detailForm", detailForm);
    return "product/detailForm";
  }

  @GetMapping
  public String findAll(Model model, HttpServletRequest request) {
    log.info("findAll() 호출됨!");
    log.info("requestURI={}", request.getRequestURI());

    List<Product> list = productSVC.findAll();
    List<AllForm> all = new ArrayList<>();
    for(Product product :list){
      AllForm allForm = new AllForm();
      allForm.setPid(product.getPid());
      allForm.setPname(product.getPname());
      all.add(allForm);
    }
    model.addAttribute("all",all);

    return "product/all";
  }

  @DeleteMapping("/{id}")
  public String deleteById(@PathVariable("id") Long id){
    int deletedRowCnt = productSVC.deleteById(id);
    return "redirect:/products";
  }

  @GetMapping("/{id}")
  public String updateForm(@PathVariable("id") Long id, Model model){
    log.info("updateForm() 호출됨!");
    Optional<Product> findedProduct = productSVC.findById(id);
    Product product = findedProduct.orElseThrow();

    UpdateForm updateForm = new UpdateForm();
    updateForm.setPid(product.getPid());
    updateForm.setPname(product.getPname());
    updateForm.setQuantity(product.getQuantity());
    updateForm.setPrice(product.getPrice());

    model.addAttribute("updateForm", updateForm);
    return "product/updateForm";
  }

  @PatchMapping("/{id}")
  public String update(@PathVariable("id") Long pid, @Valid @ModelAttribute UpdateForm updateForm,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    log.info("update() 호출됨!");
    log.info("updateForm={}",updateForm);

    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/updateForm";
    }

    if(updateForm.getQuantity() > 2000) {
      bindingResult.rejectValue("quantity","product",new Object[]{2000},null);
    }

    if(updateForm.getQuantity() * updateForm.getPrice() > 20_000_000L) {
      bindingResult.reject("totalPrice",new Object[]{2000},null);
    }

    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/updateForm";
    }

    Product product = new Product();
    product.setPname(updateForm.getPname());
    product.setQuantity(updateForm.getQuantity());
    product.setPrice(updateForm.getPrice());
    int updatedRow = productSVC.updateById(pid, product);

    redirectAttributes.addAttribute("id",pid);
    return "redirect:/products/{id}/detail";
  }
}
