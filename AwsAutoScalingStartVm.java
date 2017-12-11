import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;


public class AwsAutoScalingStartVm {
	List<String> instances = new ArrayList<String>();
	AmazonEC2Client ec2 ;
	AmazonCloudWatchClient acwac;
	AmazonElasticLoadBalancingClient loadBalance;
	static String dcInstanceId="";
	AmazonAutoScalingClient aASC;
	AwsAutoScalingStartVm(BasicAWSCredentials bawsc){
		 ec2 = new AmazonEC2Client(bawsc);
		 acwac = new AmazonCloudWatchClient(bawsc);
		 loadBalance = new  AmazonElasticLoadBalancingClient(bawsc);
		 aASC  = new AmazonAutoScalingClient(bawsc);
	     Region usEast1 = Region.getRegion(Regions.US_EAST_1);
	     ec2.setRegion(usEast1);
	}
	
	String createSecurityGroup(String groupName, String groupDescription){
        // Create a new security group.
		String groupId="";
        try {
            CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest(
            		groupName, groupDescription);
            CreateSecurityGroupResult result = ec2
                    .createSecurityGroup(securityGroupRequest);
           
            groupId= result.getGroupId();
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            createTagsRequest.withResources(result.getGroupId()).withTags(new Tag("Project","2.1"));
            System.out.println(String.format("Security group created: [%s]",
                   result.getGroupId()));
            ec2.createTags(createTagsRequest);
        } catch (AmazonServiceException ase) {
            // Likely this means that the group is already created, so ignore.
            System.out.println(ase.getMessage());
        }

        String ipAddr = "0.0.0.0/0";

        // Create a range that you would like to populate.
        List<String> ipRanges = Collections.singletonList(ipAddr);

        // Open up port all for TCP traffic to the associated IP from above (e.g. ssh traffic).
        IpPermission ipPermission = new IpPermission()
        	
                .withIpProtocol("-1")
                .withFromPort(new Integer(0))
                .withToPort(new Integer(65535)) 
                .withIpRanges(ipRanges);
        		

        List<IpPermission> ipPermissions = Collections.singletonList(ipPermission);

        try {
            // Authorize the ports to the used.
            AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(
            		groupName, ipPermissions);
            ec2.authorizeSecurityGroupIngress(ingressRequest);
            System.out.println(String.format("Ingress port authroized: [%s]",
                    ipPermissions.toString()));
        } catch (AmazonServiceException ase) {
            // Ignore because this likely means the zone has already been authorized.
            System.out.println(ase.getMessage());
        }
        
        return groupId;
  }
	

	
	String createInstance( String imageId, String instanceType,String groupName) throws Exception{

		//Create Instance Request
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.setMonitoring(true);
		
		//Configure Instance Request
		runInstancesRequest.withImageId(imageId)
		.withInstanceType(instanceType)
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName(CredentialAws.keyID)
		.withSecurityGroups(groupName);
	
		//Launch Instance
		RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
		
		//Get Instance ID of Instance:
		com.amazonaws.services.ec2.model.Instance instance = runInstancesResult.getReservation().getInstances().get(0);
		dcInstanceId=instance.getInstanceId();
		String dnsName = "";
				
		//Add a Tag to the Instance
		CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		createTagsRequest.withResources(instance.getInstanceId())
					     .withTags(new Tag("Project","2.1"));
		
		ec2.createTags(createTagsRequest);
		
		while(true){
			
		   DescribeInstancesRequest request =  new DescribeInstancesRequest();
		   request.withInstanceIds(instance.getInstanceId());
		   DescribeInstancesResult result = ec2.describeInstances(request);
		   List<Reservation> reservations = result.getReservations();
	       List<Instance> instances;
	       for(Reservation res : reservations){
	           instances = res.getInstances();
	           for(Instance ins : instances){
	        	   if(ins.getInstanceId().equals(instance.getInstanceId())){
	        		   dnsName= ins.getPublicDnsName();
	        		   break;
	        	   }

	           }
	       }
			
			if(dnsName.equals("")||dnsName.equals(null)){
				Thread.sleep(5000);
			}
			else{
				break;
			}
		}
		Thread.sleep(30000);
		return dnsName;
	}
	
	String createElasticLoadBalance(String groupName,String lgDns) throws InterruptedException{
		
		// create new load balance
		CreateLoadBalancerRequest instance= new CreateLoadBalancerRequest();
		instance.setLoadBalancerName("loadBalance");
		instance.withSecurityGroups(groupName);
		instance.withAvailabilityZones("us-east-1d");
		Collection<com.amazonaws.services.elasticloadbalancing.model.Tag> tags = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Tag>();
		com.amazonaws.services.elasticloadbalancing.model.Tag tag = new com.amazonaws.services.elasticloadbalancing.model.Tag();
		tag.setKey("Project");
		tag.setValue("2.1");
		tags.add(tag);
		instance.withTags(tags);
		Listener l = new Listener();
		l.setInstanceProtocol("http");
		l.setInstancePort(80);
		l.setLoadBalancerPort(80);
		l.setProtocol("http");
		
		
		instance.withListeners(l);
		
		//add health check
		HealthCheck healthCheck = new HealthCheck();
		healthCheck.setTimeout(5);
		healthCheck.setInterval(8);
		healthCheck.setUnhealthyThreshold(2);
		healthCheck.setHealthyThreshold(4);
		healthCheck.setTarget("HTTP:80/heartbeat?lg=" + lgDns);
		ConfigureHealthCheckRequest chcr = new ConfigureHealthCheckRequest();
		chcr.withHealthCheck(healthCheck);
		chcr.withLoadBalancerName("loadBalance");

		
		CreateLoadBalancerResult clbr = loadBalance.createLoadBalancer(instance);
		String lbDnsName = clbr.getDNSName();
		
		
		
		
		loadBalance.configureHealthCheck(chcr);
		
		
		// create connection draining
	/*	ConnectionDraining cd = new ConnectionDraining();
		cd.setEnabled(true);
		cd.setTimeout(300);
		LoadBalancerAttributes la = new LoadBalancerAttributes();
		la.setConnectionDraining(cd);
		ModifyLoadBalancerAttributesRequest modify = new ModifyLoadBalancerAttributesRequest();
		modify.setLoadBalancerAttributes(la);
		modify.setLoadBalancerName("loadBalance");
		loadBalance.modifyLoadBalancerAttributes(modify);*/
		Thread.sleep(30000);	
		return lbDnsName;
	}
	
	void createAutoScaling(String dcInstanceId1) throws InterruptedException{
		//create auto scaling group
		com.amazonaws.services.autoscaling.model.Tag  tag = new com.amazonaws.services.autoscaling.model.Tag();
		tag.setKey("Project");
		tag.setValue("2.1");
		CreateAutoScalingGroupRequest casgr = new CreateAutoScalingGroupRequest();
		casgr.setInstanceId(dcInstanceId1);
		casgr.withTags(tag);
		casgr.setAutoScalingGroupName("AutoScalingGroup");
		casgr.setDesiredCapacity(4);
		casgr.setMinSize(2);
		casgr.withLoadBalancerNames("loadBalance");
		casgr.setHealthCheckType("ELB");
		casgr.setHealthCheckGracePeriod(10);
		casgr.setMaxSize(8);
		casgr.setDefaultCooldown(10);
		casgr.withAvailabilityZones("us-east-1d");
	
		aASC.createAutoScalingGroup(casgr);
		
		//set up the policy
		PutScalingPolicyRequest  psprAdd = new PutScalingPolicyRequest();
		psprAdd.setScalingAdjustment(1);
		psprAdd.setAutoScalingGroupName("AutoScalingGroup");
		psprAdd.setCooldown(10);
		psprAdd.setPolicyName("increase");
		psprAdd.setAdjustmentType("ChangeInCapacity");
		psprAdd.setPolicyType("SimpleScaling");	
	
		// set up the policy	
		PutScalingPolicyRequest  psprMinus = new PutScalingPolicyRequest();
		psprMinus.setScalingAdjustment(-1);
		psprMinus.setAutoScalingGroupName("AutoScalingGroup");
		psprMinus.setCooldown(10);
		psprMinus.setAdjustmentType("instances");
		psprMinus.setPolicyName("decrease");
		psprMinus.setAdjustmentType("ChangeInCapacity");
		psprMinus.setPolicyType("SimpleScaling");
			
		// put the policy into auto scaling group
		PutScalingPolicyResult pspr= aASC.putScalingPolicy(psprAdd);
		String increasePolicyARN = pspr.getPolicyARN();
		pspr = aASC.putScalingPolicy(psprMinus);
		String decreasePolicyARN = pspr.getPolicyARN();
		
		//create alarm
		PutMetricAlarmRequest pmaD = new PutMetricAlarmRequest();
		pmaD.setActionsEnabled(true);
		pmaD.withAlarmActions(decreasePolicyARN);
		pmaD.setMetricName("CPUUtilization");
		pmaD.setPeriod(60);
		pmaD.setAlarmName("decreaseAlarm");
		pmaD.withEvaluationPeriods(1);
		pmaD.withComparisonOperator(ComparisonOperator.LessThanOrEqualToThreshold);
		pmaD.withThreshold(30d);
		pmaD.withNamespace("AWS/EC2");
		pmaD.withStatistic(Statistic.Average);
		pmaD.withUnit(StandardUnit.Percent);
		pmaD.withDimensions(new Dimension().withName("AutoScalingGroupName1").withValue("AutoScalingGroup"));
		
		PutMetricAlarmRequest pmaA = new PutMetricAlarmRequest();
		pmaA.setActionsEnabled(true);
		pmaA.withAlarmActions(increasePolicyARN);
		pmaA.setMetricName("CPUUtilization");
		pmaA.setPeriod(60);
		pmaA.setAlarmName("increaseAlarm");
		pmaA.withEvaluationPeriods(1);
		pmaA.withComparisonOperator(ComparisonOperator.GreaterThanOrEqualToThreshold);
		pmaA.withThreshold(40d);
		pmaA.withNamespace("AWS/EC2");
		pmaA.withStatistic(Statistic.Average);
		pmaA.withUnit(StandardUnit.Percent);
		pmaA.withDimensions(new Dimension().withName("AutoScalingGroupName2").withValue("AutoScalingGroup"));
		
	
		acwac.putMetricAlarm(pmaA);
		acwac.putMetricAlarm(pmaD);	
		
		Thread.sleep(30000);
	}
	
	void terminateInstance(String instanceId,   String groupId2) throws InterruptedException{				
			
			// delete autoscaling group
			DeleteAutoScalingGroupRequest tiisg = new DeleteAutoScalingGroupRequest();
			tiisg.withAutoScalingGroupName("AutoScalingGroup");
			tiisg.withForceDelete(true);
			aASC.deleteAutoScalingGroup(tiisg);			
			
			Thread.sleep(100000);
			
			//delete launch request
			DeleteLaunchConfigurationRequest dlcr = new DeleteLaunchConfigurationRequest();
			dlcr.withLaunchConfigurationName("AutoScalingGroup");
			aASC.deleteLaunchConfiguration(dlcr);
			
			Thread.sleep(100000);
			
			// delete instance
			TerminateInstancesRequest terminateInstancesRequest= 
					new TerminateInstancesRequest();		
			terminateInstancesRequest.withInstanceIds(instanceId);
			ec2.terminateInstances(terminateInstancesRequest);
			
			Thread.sleep(120000);
			
			// delete elb
			DeleteLoadBalancerRequest dlbr = new DeleteLoadBalancerRequest();
			dlbr.withLoadBalancerName("loadBalance");
			loadBalance.deleteLoadBalancer(dlbr);
			
			Thread.sleep(120000);	
			
			//delete security group
			DeleteSecurityGroupRequest dsgr2 = new DeleteSecurityGroupRequest();
			dsgr2.withGroupId(groupId2);
			ec2.deleteSecurityGroup(dsgr2);
		
	}
	
	public static void main(String[] args) throws Exception {
		// Get Account Id and security Key
		Properties properties = new Properties();
		properties.load(AwsAutoScalingStartVm.class.getResourceAsStream("/AwsCredentials.Properties"));
		BasicAWSCredentials bawsc = 
				new BasicAWSCredentials(properties.getProperty("accessKey"),
						properties.getProperty("secretKey"));
		
		AwsAutoScalingStartVm s = new AwsAutoScalingStartVm(bawsc);
		String  groupId1 = s.createSecurityGroup("Project2_1_1","Project2_1_1");
		String  groupId2 = s.createSecurityGroup("Project2_1_2","Project2_1_2");
		
		// start load generator
    	String lgDns = s.createInstance("ami-8ac4e9e0", "m3.medium","Project2_1_1");
     	
     	System.out.println(lgDns); 
     	
     	// start ELB 
		String elbDnsName = s.createElasticLoadBalance(groupId2,lgDns);
		System.out.println("elbDnsName" +elbDnsName);

		Thread.sleep(300000);
		
		// start data center machine
		String vmDnsAdditional = s.createInstance("ami-349fbb5e", "m3.medium","Project2_1_2");
		
		String dcInstanceId1=dcInstanceId; 
		// create autoscaling, policy and alarm
		s.createAutoScaling(dcInstanceId1);
		
		System.out.println("CreateAll");
		
		 String logInUrl = "http://"+lgDns+"/password?passwd="+CredentialAws.passWord+"&andrewId=" + CredentialAws.andrewId;
			 System.out.println(logInUrl);
		        //enter password on LG
		        while(true){
					URL startTestUrl = new URL(logInUrl);
					HttpURLConnection conn = (HttpURLConnection)startTestUrl.openConnection();					
		        	try{
		        		conn.connect();
						System.out.println(logInUrl);
						//try whether server will send me the response
						if(conn.getResponseCode()!=200){
							conn.disconnect();
							continue;
						}													
						System.out.println("Success");							
						break;		
				        }
		        	catch(java.net.SocketException e)
		        	{
		        		Thread.sleep(30000);
		        		conn.disconnect();
		        	}
		        }
		        
		        //warmUp first 
		        while(true){
		        	String warmUrl = "http://"+lgDns+"/warmup?dns="+ elbDnsName;
		        	URL startTestUrl = new URL(warmUrl);
					HttpURLConnection conn = (HttpURLConnection)startTestUrl.openConnection();	
					try{
						conn.connect();
						if(conn.getResponseCode()==200)
						{
						System.out.println("warmup starts");
						//warm up 15 minutes
						Thread.sleep(900000);
						System.out.println("warmup is done");
						conn.disconnect();
						break;
						}
						else{
							Thread.sleep(30000);
							conn.disconnect();
							System.out.println("warmup connect failed");
						}
					}
					catch(Exception e){
						Thread.sleep(30000);
						conn.disconnect();
						System.out.println("WarmUp Failed");
					}	
		        }
		        
		        //warmUp second
		      while(true){
		        	String warmUrl = "http://"+lgDns+"/warmup?dns="+ elbDnsName;
		        	URL startTestUrl = new URL(warmUrl);
					HttpURLConnection conn = (HttpURLConnection)startTestUrl.openConnection();	
					try{
						conn.connect();
						if(conn.getResponseCode()==200)
						{
						System.out.println("warmup starts");
						//warm up 15 minutes
						Thread.sleep(900000);
						System.out.println("warmup is done");
						conn.disconnect();
						break;
						}
						else{
						    Thread.sleep(30000);
							conn.disconnect();
							System.out.println("warmup connect failed");
						}
					}
					catch(Exception e){
					    Thread.sleep(30000);
						conn.disconnect();
						System.out.println("WarmUp Failed");
					}	
		        }
		        
				
		        //StartTestServer
		        
		        String getLogStr=null;
		        while(true){
					//start to test. When we can connect to URL.Print testid. Otherwise, just re-connect
					String linkUrlAddress="http://"+ lgDns + "/junior?dns="+ elbDnsName;
					URL linkUrl = new URL(linkUrlAddress);
					HttpURLConnection conn2=(HttpURLConnection) linkUrl.openConnection();
					conn2.connect();
					if(conn2.getResponseCode()!=400)
					try{
					BufferedReader in = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
					String readLine=null;
					while((readLine=in.readLine())!=null){
						if(readLine.indexOf("log")!=-1){
							getLogStr=readLine.substring(readLine.indexOf("href='/")+7,readLine.lastIndexOf(".log'")+4);
							System.out.println(readLine);
							System.out.println(getLogStr);
							String textId = readLine.substring(readLine.indexOf("href='/")+21,readLine.lastIndexOf(".log'")+4);
							System.out.println(textId);
						}
					}
						break;
					}
					catch (Exception e) {	
						//e.printStackTrace();
					}
					else{
					Thread.sleep(30000);
					conn2.disconnect();
					System.out.println("fail to connect to test url. Will retry");
				}
			}
		        
	System.out.println("The testing is running!");
	//wait 1 hours until test complete. then terminate all;	        
     Thread.sleep(3600000); 
     //start termination program
	 s.terminateInstance(dcInstanceId, groupId2);
	 System.out.println("Done for everything"); 
	
	}
}
