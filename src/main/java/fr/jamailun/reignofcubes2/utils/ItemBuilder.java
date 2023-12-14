package fr.jamailun.reignofcubes2.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * I copied this from one of my repos. it was 1.15 ...
 * So yeah, string are deprecated, but damn: Component are a freaking hassle to use.
 * <br/>
 * I'll modernize this later. Maybe.
 */
public class ItemBuilder {

    private final ItemStack item;

    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material to create the ItemBuilder with.
     */
    public ItemBuilder(Material m){
        this(m, 1);
    }
    /**
     * Create a new ItemBuilder over an existing itemstack.
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is){
        if(is == null) {
            this.item = new ItemStack(Material.AIR);
            return;
        }
        this.item = new ItemStack(is);
    }

    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }
    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount){
        item = new ItemStack(m, amount);
    }
    /**
     * Create a new ItemBuilder from scratch.
     * @since 1.9 deprecated
     * @deprecated use old bukkit feature
     * @param m The material of the item.
     * @param amount The amount of the item.
     * @param durability The durability of the item.
     */
    public ItemBuilder(Material m, int amount, byte durability){
        item = new ItemStack(m, amount, durability);
    }

    public ItemBuilder(PotionEffectType type, int duration, int amplifier, Color color, boolean splash, int amount) {
        item = new ItemStack(splash ? Material.SPLASH_POTION : Material.POTION, amount);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(color);
        meta.addCustomEffect(new PotionEffect(type, duration*20, amplifier), true);
        meta.setDisplayName(ChatColor.WHITE + "Potion of " + type.getName().toLowerCase());
        item.setItemMeta(meta);
    }
    /**
     * Clone the ItemBuilder into a new one.
     * @return The cloned instance.
     */
    public ItemBuilder clone(){
        return new ItemBuilder(item);
    }
    /**
     * Change the durability of the item.
     * @param dur The durability to set it to.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setDurability(short dur){
        item.setDurability(dur);
        return this;
    }

    public ItemBuilder addAttribute(Attribute attribute, double amount, Operation op, EquipmentSlot slot) {
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.toString(), amount, op, slot));
        item.setItemMeta(im);
        return this;
    }


    /**
     * Set the displayname of the item.
     * @param name The name to change it to.
     */
    public ItemBuilder setName(String name){
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setName(Component name){
        ItemMeta im = item.getItemMeta();
        im.displayName(name);
        item.setItemMeta(im);
        return this;
    }



    /**
     * Add an unsafe enchantment.
     * @param enchantment The enchantment to add.
     * @param level The level to put the enchantment on.
     */
    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level){
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }
    /**
     * Remove a certain enchant from the item.
     * @param enchantment The enchantment to remove
     */
    public ItemBuilder removeEnchantment(Enchantment enchantment){
        item.removeEnchantment(enchantment);
        return this;
    }
    /**
     * Set the skull owner for the item. Works on skulls only.
     * @param owner The name of the skull's owner.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setSkullOwner(String owner){
        try{
            SkullMeta im = (SkullMeta) item.getItemMeta();
            im.setOwner(owner);
            item.setItemMeta(im);
        }catch(ClassCastException expected){}
        return this;
    }
    /**
     * Add an enchant to the item.
     * @param enchantment The enchantment to add
     * @param level The level
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level){
        ItemMeta im = item.getItemMeta();
        im.addEnchant(enchantment, level, true);
        item.setItemMeta(im);
        return this;
    }
    /**
     * Add multiple enchants at once.
     * @param enchantments The enchants to add.
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments){
        item.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder addUnsafeEnchantments(Map<Enchantment, Integer> enchantments){
        for(Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            addUnsafeEnchantment(enchantment.getKey(), enchantment.getValue());
        }
        return this;
    }

    /**
     * Sets an item as Unbreakable
     */
    public ItemBuilder setUnbreakable() {
        return setUnbreakable(true);
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        if(item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(String... lore){
        ItemMeta im = item.getItemMeta();
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return this;
    }
    public ItemBuilder setLore(String lore){
        if(lore.isEmpty())
            return this;
        return setLore(new String[] {ChatColor.GRAY + lore});
    }

    public List<String> getLore() {
        if(!item.hasItemMeta())
            return new ArrayList<>();
        if(!item.getItemMeta().hasLore())
            return new ArrayList<>();
        return item.getItemMeta().getLore();
    }

    public ItemBuilder shine() {
        if( ! item.getEnchantments().isEmpty() )
            return this;
        addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        addItemFlag(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = item.getItemMeta();
        im.setLore(lore);
        item.setItemMeta(im);
        return this;
    }
    public ItemBuilder resetLore() {
        if(!item.hasItemMeta())
            return this;
        ItemMeta im = item.getItemMeta();
        im.setLore(new ArrayList<>());
        item.setItemMeta(im);
        return this;
    }
    /**
     * Remove a lore line.
     * @param line The lore to remove.
     */
    public ItemBuilder removeLoreLine(String line){
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if(!lore.contains(line))return this;
        lore.remove(line);
        im.setLore(lore);
        item.setItemMeta(im);
        return this;
    }
    /**
     * Remove a lore line.
     * @param index The index of the lore line to remove.
     */
    public ItemBuilder removeLoreLine(int index){
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if(index<0||index>lore.size())return this;
        lore.remove(index);
        im.setLore(lore);
        item.setItemMeta(im);
        return this;
    }
    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    public ItemBuilder addLoreLine(String line){
        if(line.isEmpty())
            return this;
        ItemMeta im = item.getItemMeta();
        if(im == null) {
            ReignOfCubes2.error("Invalid item... NO item meta ! " + item);
            return this;
        }

        List<String> lore = new ArrayList<>();
        if(im.hasLore())
            lore = new ArrayList<>(im.getLore());
        lore.add(ChatColor.GRAY + line);
        im.setLore(lore);
        item.setItemMeta(im);
        return this;
    }
    /**
     * Add a lore line.
     * @param line The lore line to add.
     * @param pos The index of where to put it.
     */
    public ItemBuilder addLoreLine(String line, int pos){
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, ChatColor.GRAY + line);
        im.setLore(lore);
        item.setItemMeta(im);
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     * @param color The color to set it to.
     */
    public ItemBuilder setLeatherArmorColor(Color color){
        try{
            LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
            im.setColor(color);
            item.setItemMeta(im);
        }catch(ClassCastException expected){}
        return this;
    }
    /**
     * Retrieves the itemstack from the ItemBuilder.
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack(){
        return item;
    }

    public ItemBuilder addItemFlag(ItemFlag... flags) {
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(flags);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder hideAll() {
        return addItemFlag(ItemFlag.values());
    }

    public ItemBuilder setPotionColor(Color color) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return this;
    }
}
