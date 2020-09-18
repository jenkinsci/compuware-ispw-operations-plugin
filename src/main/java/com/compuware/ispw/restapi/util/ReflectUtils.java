package com.compuware.ispw.restapi.util;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import com.compuware.ispw.restapi.action.IAction;
import com.compuware.ispw.restapi.action.IspwAction;
import com.compuware.ispw.restapi.action.IspwCommand;

/**
 * Use annotation and reflection to read and instantiate ISPW actions
 * 
 * @author Sam Zhou
 *
 */
public class ReflectUtils {
	private static Logger logger = Logger.getLogger(ReflectUtils.class);

	public static void reflectSetter(Object object, String name, Object value) {

		List<Field> fields = FieldUtils.getAllFieldsList(object.getClass());
		for (Field field : fields) {

			String fieldName = field.getName();
			String jsonName = fieldName; // default to field name
			if (field.isAnnotationPresent(XmlElement.class)) {
				XmlElement xmlElement = field.getAnnotation(XmlElement.class);
				jsonName = xmlElement.name(); // use annotation name if presented
			}

			logger.info("json.name=" + jsonName + ", fieldName=" + fieldName + ", type=" + field.getType().getName() + ", value=" + value);
			if (jsonName.equals(name) || fieldName.equals(name)) {
				try {
					BeanUtils.setProperty(object, fieldName, value);
				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.warn("Property key " + name + "(" + jsonName + ") is invalid, cannot be set to class "
							+ object.getClass().getName() + "as value [" + value + "])");
				}
			}
		}
	}

	public static String reflectGetter(Object object, String name) {
		String value = StringUtils.EMPTY;
		List<Field> fields = FieldUtils.getAllFieldsList(object.getClass());
		
		for (Field field : fields) {

			String fieldName = field.getName();
			String jsonName = fieldName; // default to field name
			if (field.isAnnotationPresent(XmlElement.class)) {
				XmlElement xmlElement = field.getAnnotation(XmlElement.class);
				jsonName = xmlElement.name(); // use annotation name if presented
			}

			if (jsonName.equals(name)) {
				try {
					value = BeanUtils.getProperty(object, fieldName);
					logger.info("json.name=" + jsonName + ", type=" + field.getType().getName() + ", value=" + value);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					logger.warn("Property key " + name + "(" + jsonName + ") is invalid, cannot get value for class "
							+ object.getClass().getName());
				}
			}
		}
		
		return value;
	}
	
	public static String[] listPublishedCommands() {
		ArrayList<String> commands = new ArrayList<String>();
		List<Field> fields = FieldUtils.getAllFieldsList(IspwCommand.class);

		for (Field field : fields) {
			if (field.isAnnotationPresent(IspwAction.class)) {
				IspwAction ispwAction = field.getAnnotation(IspwAction.class);
				Class<?> clazz = ispwAction.clazz();
				boolean isExposed = ispwAction.exposed();

				String command = StringUtils.EMPTY;
				try {
					command = (String) FieldUtils.readStaticField(field);

					if (isExposed) {
						commands.add(command);
					}
				} catch (IllegalAccessException e) {
					String message = String.format("Failed to read command value in field: %s, clazz: %s", command,
							clazz.getName());
					logger.error(message, e);
				}
			}
		}

		return commands.toArray(new String[commands.size()]);
	}

	public static Class<?> getCommandClass(String command) {
		Class<?> clazz = null;

		List<Field> fields = FieldUtils.getAllFieldsList(IspwCommand.class);

		for (Field field : fields) {
			if (field.isAnnotationPresent(IspwAction.class)) {
				String definedCommand = StringUtils.EMPTY;
				try {
					definedCommand = (String) FieldUtils.readStaticField(field);
				} catch (IllegalAccessException e) {
					String message = String.format("Failed to read command value in field: %s", command);
					logger.error(message, e);
				}

				if (definedCommand.equals(command)) {
					IspwAction ispwAction = field.getAnnotation(IspwAction.class);
					clazz = ispwAction.clazz();
					String message = String.format("Reflect to get command %s -> class %s", command, clazz.getName());
					logger.info(message);
				}
			}
		}

		return clazz;
	}

	public static IAction createAction(String command, PrintStream log) {
		IAction action = null;
		Class<?> clazz = getCommandClass(command);
		String clazzName = "[No match class]";
		String actionName = "[No match action]";

		if (clazz != null) {
			clazzName = clazz.getName();
			try {
				action = (IAction) ConstructorUtils.invokeConstructor(clazz, log);
				if (action != null) {
					actionName = action.toString();
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
					| InstantiationException e) {
				String message = String.format("Failed to instantiate command: %s from action class: %s", command,
						clazzName);
				log.println(message);

				logger.error(message, e);
			}
		}

		String message = String.format("Reflect to instantiate command %s -> Class %s -> instance %s", command, clazzName,
				actionName);
		logger.info(message);

		return action;
	}

	/**
	 * Determines whether the specified <code>IAction</code> is non-null therefore
	 * has been instantiated via reflection.
	 * 
	 * @param iAction
	 *            The <code>IAction</code>
	 * @return whether or not the IAction has been instantiated
	 */
	public static boolean isActionInstantiated(IAction iAction) {
		if (iAction == null) {
			return false;
		} else {
			return true;
		}
	}

	/* (non-Javadoc) */
	public static <T> T newInstance(Class<T> clazz) {
		T t = null;

		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		}

		return t;
	}

}
