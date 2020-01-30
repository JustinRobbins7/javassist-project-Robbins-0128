package ex04;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.NotFoundException;
import util.UtilMenu;

public class exClassLoader {

	private static final String PKG_NAME = "target" + ".";
	
	public static void main(String[] args) {
		String[] tarClasses;
		boolean validInputGiven = false;

		// Get Class Names
		do {
			System.out.println("===================================================================");
			System.out.println("EX04 - Please enter three class names delimited by : to be modified");
			System.out.println("===================================================================");

			tarClasses = UtilMenu.getArguments();

			if (tarClasses.length != 3) {
				System.out.println("[WRN] Invalid Input Size!!");
			} else {
				validInputGiven = true;
			}
		} while (!validInputGiven);

		try {
			for(int i = 0; i < tarClasses.length; i++) {
				tarClasses[i] = PKG_NAME + tarClasses[i];
			}
			
			ClassPool pool = ClassPool.getDefault();
			CtClass spc = pool.get(tarClasses[0]);
			CtClass sb1 = pool.get(tarClasses[1]);
			CtClass sb2 = pool.get(tarClasses[2]);
			
			setSuperclass(sb1, tarClasses[0], pool);
			setSuperclass(sb2, tarClasses[0], pool);
			
			ArrayList<String> moddedMethods = new ArrayList<String>();
			do {
				System.out.println("===================================================================");
				System.out.println("EX04 - Please enter an usage, an increment, and a getter method.   ");
				System.out.println("===================================================================");
				
				String[] tarMethods = UtilMenu.getArguments();
				
				if (tarMethods.length != 3) {
					System.out.println("[WRN] Invalid Input Size!!");
					continue;
				}
				
				if(moddedMethods.contains(tarMethods[0])) {
					System.out.println("[WRN] This method " + tarMethods[0] + " has been modified!");
					continue;
				}
				
				editMethod(sb1, tarMethods[0], tarMethods[1], tarMethods[2]);
				editMethod(sb2, tarMethods[0], tarMethods[1], tarMethods[2]);
				
				Loader load = new Loader(pool);
				Class<?> sbc1 = load.loadClass(tarClasses[1]);
				Class<?> sbc2 = load.loadClass(tarClasses[2]);
				
				Object obj1 = sbc1.newInstance();
				Object obj2 = sbc2.newInstance();
				
				Class<?> obj1class = obj1.getClass();	
				Class<?> obj2class = obj2.getClass();
				
				Method mtest1 = obj1class.getDeclaredMethod(tarMethods[0], new Class[] {});
				Method mtest2 = obj2class.getDeclaredMethod(tarMethods[0], new Class[] {});
				
				System.out.println("[EX04] Calling modded method " + tarMethods[0] + " of " + tarClasses[1]);
		        Object invoker1 = mtest1.invoke(obj1, new Object[] {});
				
		        System.out.println("[EX04] Calling modded method " + tarMethods[0] + " of " + tarClasses[2]);
		        Object invoker2 = mtest2.invoke(obj2, new Object[] {});
		        
				moddedMethods.add(tarMethods[0]);
				
			} while (true);
		} catch(NotFoundException | CannotCompileException | ClassNotFoundException 
				| IllegalAccessException | InstantiationException | NoSuchMethodException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	// Adds superclass to the given class
	static void setSuperclass(CtClass curClass, String superClass, ClassPool pool)
			throws NotFoundException, CannotCompileException {
		curClass.defrost();
		curClass.setSuperclass(pool.get(superClass));
		System.out.println("[DBG] set superclass: " + curClass.getSuperclass().getName() + //
				", subclass: " + curClass.getName());
	}
	
	static void editMethod(CtClass cn, String useMethod, String incMethod, String getMethod) 
			throws NotFoundException, CannotCompileException {
		cn.defrost();
		CtMethod m1 = cn.getDeclaredMethod(useMethod);
		char varc = incMethod.charAt(3);
		
		m1.insertBefore("{ " + 
				incMethod + "(); " +
				"System.out.println(\"" + varc + ": \" + " + getMethod + "()); " +
				" }" 
				);
		
		
	}
}