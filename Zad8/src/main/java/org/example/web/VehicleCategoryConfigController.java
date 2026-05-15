package org.example.web;

import org.example.models.VehicleCategoryConfig;
import org.example.services.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class VehicleCategoryConfigController
{

    private final VehicleCategoryConfigService categoryService;

    public VehicleCategoryConfigController(VehicleCategoryConfigService categoryService)
    {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list()
    {

        return categoryService.findAllCategories();

    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig getCategory(@PathVariable String category)
    {

        return categoryService.getByCategory(category);

    }


}
