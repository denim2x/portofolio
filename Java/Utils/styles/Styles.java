/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.styles;

/**
 *
 * @author Cristian
 */
public class Styles {
	public static String getStyle(String name){
		return Styles.class.getResource(name).toExternalForm();
	}
}