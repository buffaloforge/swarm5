package main;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class hello
 */
@WebServlet("/hello")
public class helloShift extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public helloShift()
	{
		super();
		// TODO Auto-generated constructor stub
	}
   
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		response.getWriter().print(" noapples");
		String sHost = "ldap://" +"172.30.20.149"+":30182";

		try{		
			getOrgnization(response,sHost);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


/**
 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
 *      response)
 */
protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
{
	// TODO Auto-generated method stub
}

public void getOrgnization(HttpServletResponse response,String sHost)throws Exception
{
	response.getWriter().print("getting org");
	// TODO Auto-generated method stub
	Hashtable env = new Hashtable(11);
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, sHost);
	env.put(Context.SECURITY_AUTHENTICATION, "simple");
	env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=example,dc=org");
	env.put(Context.SECURITY_CREDENTIALS, "admin");

	try
	{

		checkPasswdAD("uid=jmn45,ou=Engineering,dc=example,dc=org", "doh098",response,sHost);

		String sSearchPath = "ou=Engineering,dc=example,dc=org";
		String searchfilter = "ou=Engineering";
		DirContext ctx11 = new InitialDirContext(env);
		SearchControls searchControls = new SearchControls();

		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(10);

		// pull information about the group
		NamingEnumeration<SearchResult> namingEnumeration = ctx11.search(sSearchPath,
				searchfilter, new Object[]
						{}, searchControls);
		// pull information about the user
		Attributes answer = ctx11.getAttributes("uid=jmn45,ou=Engineering,dc=example,dc=org");
		printUserAttrs(answer,response);

		String sKey = "";
		String sValue = "";
		HashMap hashMap = new HashMap();
		while (namingEnumeration.hasMoreElements())
		{
			SearchResult sr = (SearchResult) namingEnumeration.next();
			Attributes attributes = sr.getAttributes();
			System.out.println(sr.getName());
			// logger.info(attributes.getAll());

			NamingEnumeration neAllAttr = attributes.getAll();
			sKey = "";
			sValue = "";
			while (neAllAttr.hasMore())
			{
				// grab the key
				Attribute attr = (Attribute) neAllAttr.next();
				sKey = attr.getID();

				// grab the value
				NamingEnumeration neValues = attr.getAll();
				while (neValues.hasMore())
					sValue = (String) neValues.next();
				hashMap.put(sKey, sValue);
			}
		}

		response.getWriter().print("orginzation output");
		response.getWriter().print(hashMap);

		while (namingEnumeration.hasMore())
		{
			SearchResult sr = namingEnumeration.next();
			System.out.println("DN: " + sr.getName());
			System.out.println(sr.getAttributes().get("uid"));
			// System.out.println("Password:" + new String((byte[])
			// sr.getAttributes().get("userPassword").get()));

		}
		ctx11.close();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}

}

static void printUserAttrs(Attributes attrs, HttpServletResponse response) throws Exception
{	response.getWriter().print("printUserAttrs");
	if (attrs == null)
	{
		response.getWriter().print("user attributes");
	}
	else
	{
		/* Print each attribute */
		try
		{response.getWriter().print("User attributes");
		for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();)
		{
			Attribute attr = (Attribute) ae.next();
			response.getWriter().print("attribute: " + attr.getID());

			/* print each value */
			for (NamingEnumeration e = attr.getAll(); e.hasMore(); response.getWriter()
					.print("valuer: " + e.next()))
				; 

		}
		}
		catch (NamingException e)
		{
			e.printStackTrace();
		}
	}
}

public static void checkPasswdAD(String sOu, String sPassword,HttpServletResponse response,String sHost) throws Exception
{
	// sOu =
	// "";
	try
	{
		Hashtable htEnvinm = new Hashtable<String, String>(11);
		DirContext ctx = null;
		// logger.info("ou ::: " + (String) hashMap.get("ou"));
		htEnvinm.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// htEnvinm.put(Context.PROVIDER_URL, "ldap://ldap1:389");
		htEnvinm.put(Context.SECURITY_AUTHENTICATION, "simple");
		htEnvinm.put(Context.SECURITY_PRINCIPAL, sOu);
		htEnvinm.put(Context.SECURITY_CREDENTIALS, sPassword);
		htEnvinm.put(Context.PROVIDER_URL, sHost);

		ctx = new InitialDirContext(htEnvinm);
		ctx.close();
		response.getWriter().print("successfuld password");
	}
	catch (Exception e)
	{
		throw new Exception("Failed to bind with user credentials: " + e.toString());
	}
}

}

