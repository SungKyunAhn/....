package com.aimir.fep.iot.model.vo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenuTree {
    Map<Integer, MenuVO> menus = null;
    List<MenuVO> topMenu = null;
    
    private static MenuTree menuTree;
    public static MenuTree getInstance(){
        if(menuTree == null){
        	menuTree = new MenuTree();
        }
        return menuTree;
    }

//    class Menu {
//    	  /** 메뉴번호 */
//        private int menuNo;
//        /** 상위메뉴번호 */
//        private int upperMenuId;
//        /** 메뉴명 */
//        private String menuNm;
//        List<Menu> children;
//
//        public Menu(String no, String id, String menuNm) {
//  
//            try {
//                int menuNo = Integer.parseInt(no);
//                int upperMenuId = 0;
//                if (!"Null".equals(id)) {
//                	upperMenuId = Integer.parseInt(id);
//                }
//                this.menuNo = menuNo;
//                this.upperMenuId = upperMenuId;
//                this.menuNm = menuNm;
//            } catch (Exception e) {
//                System.out.println("Unable to create Menu as the data is " + menuNo + " " + upperMenuId + " " + menuNm);
//            }
//        }
//
//		public List<Menu> getChildren() {
//			return children;
//		}
//
//		public void setChildren(List<Menu> children) {
//			this.children = children;
//		}
//
//		public int getMenuNo() {
//			return menuNo;
//		}
//
//		public void setMenuNo(int menuNo) {
//			this.menuNo = menuNo;
//		}
//
//		public int getUpperMenuId() {
//			return upperMenuId;
//		}
//
//		public void setUpperMenuId(int upperMenuId) {
//			this.upperMenuId = upperMenuId;
//		}
//
//		public String getMenuNm() {
//			return menuNm;
//		}
//
//		public void setMenuNm(String menuNm) {
//			this.menuNm = menuNm;
//		}
//    }

//    public static void main(String[] args) throws IOException {
//        MenuTree thisClass = new MenuTree();
//        thisClass.makeMenuTree();
//    }

    public List<MenuVO> makeMenuTree(List<Map<String, Object>> result) throws IOException {
    	menus = new HashMap<Integer, MenuVO>();
    	topMenu = new ArrayList<MenuVO>();
    	createMenus(result);
        for(MenuVO tMenu : topMenu){
        	 buildHierarchy(tMenu);
        }
       return this.topMenu;
    }

    private void createMenus(List<Map<String, Object>> result) throws IOException {
    	for(Map<String, Object> map : result){
    		MenuVO menu = null;
    		 try{
    			 menu = new MenuVO(map.get("menuNo").toString(), map.get("upperMenuId").toString(), map.get("menuNm").toString(), map.get("chkURL").toString());
    		 } catch (Exception e) {
                 System.out.println("Unable to create Menu as the data is " + map.get("menuNm").toString());
             }
    		  menus.put(menu.getMenuNo(), menu);
              if (menu.getUpperMenuId() == 0) {
              	topMenu.add(menu);
              }
    	}
    }

    public List<MenuVO> findAllMenusByUpperMenuId(int upperMenuId) {
        List<MenuVO> sameUpperMenuIdMenus = new ArrayList<MenuVO>();
        for (MenuVO m : menus.values()) {
            if (m.getUpperMenuId() == upperMenuId) {
            	sameUpperMenuIdMenus.add(m);
            }
        }
        return sameUpperMenuIdMenus;
    }

    private void buildHierarchy(MenuVO topMenu) {
    	MenuVO menu = topMenu;
        List<MenuVO> menus1 = findAllMenusByUpperMenuId(menu.getMenuNo());
        menu.setChildren(menus1);
        if (menus1.size() == 0) {
            return;
        }

        for (MenuVO e : menus1) {
            buildHierarchy(e);
        }
    }
}
