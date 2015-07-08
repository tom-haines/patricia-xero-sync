##Notes

----

To compile the JAXB files (xero2), I used the following command:

	cd etc/
	xjc -p com.pi.xero.pataccount.xero2.jaxb -b jaxbBindings.xml v2.00

The binding file locations in jaxbBindings.xml will need to be changed.

The schema files were taken from here:

	https://github.com/XeroAPI/XeroAPI-Schemas
	
The number of schema files are different, so it is possible that the current schema files being used are outdated.
