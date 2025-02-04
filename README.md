# 🚗 Quick Park Assist
A smart parking management system designed to streamline parking spot booking, user registration, slot management, and EV charging integration. This project aims to provide a seamless parking experience with real-time monitoring, performance analysis, and quality assurance.

# 🌟 Key Features
Parking Spot Booking: Users can register, view available slots, and book parking spots easily.
User Registration & Management: Allows users to sign up and manage their profile for booking and payment.
EV Charging Integration: Supports electric vehicle charging stations along with the booking system.
Slot Management: Admins can manage and update parking slots based on availability.
# 💻 Technologies Used
Backend: Java, Spring MVC, Spring Data JPA
API Testing: Postman
Code Quality: SonarQube
Log Monitoring: Splunk
Performance Analysis: Grafana
# 🛠 Project Development
Scalable Architecture: Built using Java, Spring MVC, and Spring Data JPA, ensuring flexibility and scalability for future features.
Code Quality Assurance: Integrated SonarQube for continuous code quality analysis, ensuring maintainable and efficient code.
API Testing: Used Postman for comprehensive API testing to ensure smooth user interactions.
Real-Time Log Monitoring: Implemented Splunk for monitoring and analyzing logs in real-time, providing insights into system behavior and performance.
Performance Metrics: Integrated Grafana to visualize system performance, helping optimize resource usage and identifying bottlenecks.
# 📝 Test Coverage
The application includes detailed test cases covering key features such as user registration, parking spot booking, and slot management. Code quality is regularly analyzed to maintain a high standard of performance.

# 📈 Monitoring & Analytics
Splunk is used to gather and analyze logs in real-time, providing insights into user activity and system performance.
Grafana is employed for visualizing key metrics like system load, response times, and active users, helping in performance tuning and troubleshooting.
# 🚀 How to Run the Project
Clone the repository:

git clone https://github.com/yourusername/quick-park-assist.git
Navigate to the project directory:
cd quick-park-assist

Install dependencies and build the project:

mvn clean install
Run the application:

mvn spring-boot:run
# 👥 Collaboration
This project was developed in collaboration with a team, following Agile methodologies to ensure smooth sprint management and consistent progress. Contributions include backend development, integration of third-party services, and testing.

# 📧 Contact
For any questions or suggestions, feel free to reach out to us!

# Sonar Integration
Step - 1:
    Navigate to Sonar Downloads Page"  https://www.sonarsource.com/products/sonarqube/downloads/  " scroll down and you'll Find t
    Sonar 9.9.8 LTA, and Download the Community Version.

Step - 2:
    1. Now Extract the Zip file.
    2. Now Open that folder and navigate to bin --> windows-x86-64 
    3. Right Click on StartSonar and run-As-Administrator.
    4. Now It'll take a couple of minutes for the Sonar-Server to start.Once it is started and running It will appear like this.

Step - 3:
    1. Now Open Browser and enter the Url = "  [localhost:9000](http://localhost:9000) "
    2. For new Users the Login = "admin" and Password = ="admin" both are same. After login change the newpassword acccordingly.
    3.Now enter Manually:
    4. Enter the priject - Name : quick-park-assist. And click Set-up
    5.  Now Click Locally:
    6. Click generate  you change the expiration date as per your desire it is optional
    7. It will generrate a unique ID for Your Project:
    8. Choose Maven and copy the and credentials and store them it will be required in future(IMPORTANT):

Step - 4:
    1. Go to Your Project folder:
    2. Open Command-prompt from that folder:
    3. Now Paste the credential which you have copied from the sonar server web-site: Remove the back-ward slashes,
    and place them in a straight line and place the entire command in the "cmd"
    mvn clean verify sonar:sonar  -Dsonar.projectKey=quick-park-assist -Dsonar.host.url=http://localhost:9000 -Dsonar.login='YOUR uniqueServerID'

NOTE: If it gives any Error please configure MAVEN in your system.
If you do not know how to configure watch this video to configure MAVEN in your SYSTEM 
LINK: https://www.youtube.com/watch?v=3EfvEZ_wThc

