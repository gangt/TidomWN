package com.xiangpu.bean;


import java.util.ArrayList;
/**
 * 点击小黄点后显示的菜单对象
 * @author huangda
 *
 */
public class MenuBean {

		public String menuUrl;    //当前页面为native页面时，menuUrl为类名；当前页面为H5页面时，menuUrl为H5页面的Url
		public String menuTitle;    //所在页面的标题
		
		public ArrayList<MenuItem> menuItems;
		
		public class MenuItem extends BaseMenuItem {

			public String menuItemName;  //每一个菜单项的名字
			public String menuItemIconName;      //每一个菜单项的图标的名字
			public String menuItemSub;       //要跳转到native页面时，menuItemUrl为类名；要跳转到H5页面时，menuItemUrl为H5页面的Url
			public String menuItemType;

			public String getMenuItemName() {
				return menuItemName;
			}
			public void setMenuItemName(String menuItemName) {
				this.menuItemName = menuItemName;
			}
			public String getMenuItemIconName() {
				return menuItemIconName;
			}
			public void setMenuItemIconName(String menuItemIconName) {
				this.menuItemIconName = menuItemIconName;
			}
			public String getMenuItemSub() {
				return menuItemSub;
			}
			public void setMenuItemSub(String menuItemType,String menuItemSub) {
				this.menuItemSub = super.changeItemSub(menuItemType,menuItemSub);
			}
			public void setMenuItemType(String menuItemType) {
				this.menuItemType = menuItemType;
			}
			public String getMenuItemType(){return menuItemType;}

			@Override
			public String toString() {
				return "MenuItem [menuItemName=" + menuItemName + ", menuItemIconName=" + menuItemIconName
						+ ", menuItemUrl=" + menuItemSub + "]";
			}
			
		}

		public String getMenuUrl() {
			return menuUrl;
		}

		public void setMenuUrl(String menuUrl) {
			this.menuUrl = menuUrl;
		}

		public String getMenuTitle() {
			return menuTitle;
		}

		public void setMenuTitle(String menuTitle) {
			this.menuTitle = menuTitle;
		}

		public ArrayList<MenuItem> getMenuItems() {
			return menuItems;
		}

		public void setMenuItems(ArrayList<MenuItem> menuItems) {
			this.menuItems = menuItems;
		}
		
}
