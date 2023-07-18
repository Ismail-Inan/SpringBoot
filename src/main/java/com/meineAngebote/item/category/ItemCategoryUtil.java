package com.meineAngebote.item.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ItemCategoryUtil {

  public static List<ItemCategoryNode> getCategories() {

    List<ItemCategoryNode> categories = new ArrayList<>();

    var phones = new ItemCategoryNode(ItemCategory.PHONES);
    phones.addChild(new ItemCategoryNode(ItemCategory.SAMSUNG));
    phones.addChild(new ItemCategoryNode(ItemCategory.APPLE));
    categories.add(phones);

    var computer = new ItemCategoryNode(ItemCategory.COMPUTER);
    categories.add(computer);

    var gardening = new ItemCategoryNode(ItemCategory.GARDENING);
    categories.add(gardening);

    categories.add(new ItemCategoryNode(ItemCategory.ACCESSORIES));
    categories.add(new ItemCategoryNode(ItemCategory.JEWELRY));
    categories.add(new ItemCategoryNode(ItemCategory.CARS));
    categories.add(new ItemCategoryNode(ItemCategory.TRANSPORT));
    categories.add(new ItemCategoryNode(ItemCategory.MOVING));
    categories.add(new ItemCategoryNode(ItemCategory.BEAUTY_CARE));
    categories.add(new ItemCategoryNode(ItemCategory.CRAFTSMAN));
    categories.add(new ItemCategoryNode(ItemCategory.SPORT));

    var clothing = new ItemCategoryNode(ItemCategory.CLOTHING);
    clothing.addChild(new ItemCategoryNode(ItemCategory.MEN));
    clothing.addChild(new ItemCategoryNode(ItemCategory.WOMEN));
    categories.add(clothing);

    return categories;
  }

  public static List<ItemCategoryNode> getAllChildren(ItemCategoryNode itemCategoryNode) {
    List<ItemCategoryNode> children = itemCategoryNode.getChildren();
    if (Objects.isNull(children)) {
      return new ArrayList<>();
    }
    List<ItemCategoryNode> allChildren = new ArrayList<>(children);
    for (ItemCategoryNode child : children) {
      allChildren.addAll(getAllChildren(child));
    }
    return allChildren;
  }

  public static List<ItemCategoryNode> getAllChildren(ItemCategory itemCategory) {
    List<ItemCategoryNode> categories = getCategories();
    for (ItemCategoryNode category : categories) {
      if (category.category.equals(itemCategory)) {
        return getAllChildren(category);
      }
    }
    return new ArrayList<>();
  }

  public enum ItemCategory {

    ACCESSORIES, JEWELRY,

    PHONES, SAMSUNG, APPLE,

    COMPUTER,
    CARS,
    TRANSPORT,
    MOVING,
    SPORT,
    BEAUTY_CARE,
    GARDENING,
    CRAFTSMAN,

    CLOTHING, MEN, WOMEN;
  }

  public static class ItemCategoryNode {

    private ItemCategory category;

    private List<ItemCategoryNode> children;

    public ItemCategoryNode(ItemCategory itemCategory) {
      category = itemCategory;
    }

    public void addChild(ItemCategoryNode child) {
      if (Objects.isNull(children)) {
        children = new ArrayList<>();
      }
      children.add(child);
    }

    public ItemCategory getCategory() {
      return category;
    }

    public List<ItemCategoryNode> getChildren() {
      return children;
    }
  }

}