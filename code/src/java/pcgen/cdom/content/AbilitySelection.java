package pcgen.cdom.content;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;

public class AbilitySelection extends Selection<Ability, String>
{

	public AbilitySelection(Ability obj, String sel)
	{
		super(obj, sel);
	}

	/**
	 * Decodes the given String into an AbilitySelection. The String format to
	 * be passed into this method is defined solely by the return result of the
	 * getPersistentFormat method. There is no guarantee that the encoding is
	 * human readable, simply that the encoding is uniquely identifying such
	 * that this method is capable of decoding the String into an
	 * AbilitySelection.
	 * 
	 * @param persistentFormat
	 *            The String which should be decoded to provide an
	 *            AbilitySelection.
	 * 
	 * @return An AbilitySelection that was encoded in the given String.
	 */
	public static AbilitySelection getAbilitySelectionFromPersistentFormat(
		String persistentFormat)
	{
		StringTokenizer st =
				new StringTokenizer(persistentFormat, Constants.PIPE);
		String catString = st.nextToken();
		if (!catString.startsWith("CATEGORY="))
		{
			throw new IllegalArgumentException(
				"String in getAbilitySelectionFromPersistentFormat "
					+ "must start with CATEGORY=, found: " + persistentFormat);
		}
		String cat = catString.substring(9);
		AbilityCategory ac = SettingsHandler.getGame().getAbilityCategory(cat);
		if (ac == null)
		{
			throw new IllegalArgumentException(
				"Category in getAbilitySelectionFromPersistentFormat "
					+ "must exist found: " + cat);
		}
		String ab = st.nextToken();
		Ability a =
				Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					Ability.class, ac, ab);
		if (a == null)
		{
			throw new IllegalArgumentException(
				"Second argument in String in getAbilitySelectionFromPersistentFormat "
					+ "must be an Ability, but it was not found: "
					+ persistentFormat);
		}
		String sel = null;
		if (st.hasMoreTokens())
		{
			/*
			 * No need to check for MULT:YES/NO here, as that is checked
			 * implicitly in the construction of AbilitySelection below
			 */
			sel = st.nextToken();
		}
		else if (persistentFormat.endsWith(Constants.PIPE))
		{
			// Handle the StringTokenizer ignoring blank tokens at the end
			sel = "";
		}
		if (st.hasMoreTokens())
		{
			throw new IllegalArgumentException(
				"String in getAbilitySelectionFromPersistentFormat "
					+ "must have 2 or 3 arguments, but found more: "
					+ persistentFormat);
		}
		return new AbilitySelection(a, sel);
	}

	/**
	 * Encodes the AbilitySelection into a String sufficient to uniquely
	 * identify the AbilitySelection. This may not sufficiently encode to be
	 * stored into a file or format which restricts certain characters (such as
	 * URLs), it simply encodes into an identifying String. There is no
	 * guarantee that this encoding is human readable, simply that the encoding
	 * is uniquely identifying such that the
	 * getAbilitySelectionFromPersistentFormat method of AbilitySelection is
	 * capable of decoding the String into an AbilitySelection.
	 * 
	 * @return A String sufficient to uniquely identify the AbilitySelection.
	 */
	public String getPersistentFormat()
	{
		Ability ability = getObject();
		StringBuilder sb = new StringBuilder();
		sb.append("CATEGORY=");
		sb.append(ability.getCDOMCategory().getKeyName());
		sb.append('|');
		sb.append(ability.getKeyName());
		String selection = getSelection();
		if (selection != null)
		{
			sb.append('|');
			sb.append(selection);
		}
		return sb.toString();
	}

	public String getAbilityKey()
	{
		return getObject().getKeyName();
	}

	public boolean containsAssociation(String a)
	{
		String assoc = getSelection();
		return (a == assoc) || ((a != null) && a.equalsIgnoreCase(assoc));
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getAbilityKey());
		String selection = getSelection();
		if ((selection != null) && (selection.length() > 0))
		{
			sb.append(" (");
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}
}
