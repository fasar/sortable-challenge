sortable-challenge
==================

This application link syntatically different entities that describe the same object.

The goal is proposed by [Sortable]: http://sortable.com/blog/coding-challenge/


# Build application

To build application you need maven2.

	$ mvn package

After compilation, you get a file application/console/target/sortable-challenge-dist.zip.
It is a zip package of the application.
Deploy it in a directory $DIR.





# Application usage

You can execute $DIR/bin/package.sh for linux or $DIR\bin\challenge.sh for windows.

Usage is :

	Usage :  [OPTION] file_products file_listing
	Record Linkage of products and listing
	  -h, -help             print this message
	  -o, --output [FILE]   output file
 	                        if the output is not set, it uses 'outputDefault' in properties file
	   
	 file_product   a json file of products
	 file_listing   a json listings file
	
	properties file is conf/diffTools.properties file


