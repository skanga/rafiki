### Rafiki ####

This fork is modified to contain newer libraries like log4j and convert Chinese comments to English

Rafiki is a task scheduling library for Java

- Easy to integrated to any Java application
- Can create simple or complex trigger
- Support 10000+ tasks to run

## Installation ##

Maven

	<dependency>
	    <groupId>io.github.skanga</groupId>
	    <artifactId>rafiki-skanga</artifactId>
	    <version>1.3</version>
	</dependency>

## Build

>mvn clean compile

## Create jar file

>mvn clean package

## Publish jar file to Maven Central

>mvn clean deploy -P release

## Getting Started ##

Sample code:

	public class DemoTestManager {
		public static void main(String arg[]) throws Exception {
			Task task = new Task();
			Job job = new Job() {
				public String getName() {
					return "DelayJob";
				}
		
				public boolean execute() throws JobException {
					System.out.println("Now is : " + Long.toString(System.currentTimeMillis()));
					return true;
				}
			};
		
			task.setName("HelloJob");
			task.setJob(job);
			task.setTrigger(new CronTrigger("0-30/5 * * * * * *"));
			
			TaskContainer container = new TaskContainer();
			container.add(task);
			container.start();
		}
	}
	
More examples available [here] (./src/example/java/org/pinae/rafiki)

## License ##

rafiki is licensed under the Apache License, Version 2.0 See LICENSE for full license text
