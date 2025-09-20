# 🎓 InternBay – A Campus Internship & Job Portal  

A modern Internship and Job Portal built with **Spring Boot, Spring Security (JWT), Hibernate/JPA, MySQL, and REST APIs**.  
It connects **students, employers, and administrators** in one platform, making internship/job applications seamless.  

---

## 🚀 Technologies Used  

- **Backend:** Spring Boot 3, Spring Security (JWT), Hibernate/JPA, Lombok  
- **Database:** MySQL  
- **Authentication:** JWT-based authentication & role-based access  
- **IDE:** IntelliJ IDEA  

---

## 📚 Features  

### 🔑 Authentication & Authorization  
- Secure **JWT-based login system**  
- Role-based access (**Admin**, **Employer**, **Candidate**)  
- Session-less API security  

### 🧑‍🎓 Candidate 
- 📝 Register & manage student profile  
- 📂 Browse internships & jobs  
- 📤 Apply to opportunities  
- 📋 View application history & status updates  

### 🏢 Employer  
- 📝 Create & manage job postings  
- 📂 Filter applicants for each posting  
- ✅ Approve / reject student applications  
- 🧠 Manage employer profiles  

### 🛠️ Admin   
- 👀 Manage all users (students, employers)  
- 🗄️ Manage job categories & postings  
- 🔒 Monitor system security and usage  

---

## 📂 Project Structure  

```internbay-job-portal/
├── .idea/
├── assets/
├── docs/
├── internbayBackEnd/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/
│ │ │ │ └── lk/ijse/gdse/main/internbaybackend/
│ │ │ │ ├── config/
│ │ │ │ ├── controller/
│ │ │ │ ├── dto/
│ │ │ │ ├── entity/
│ │ │ │ ├── exceptions/
│ │ │ │ ├── repository/
│ │ │ │ ├── service/
│ │ │ │ ├── util/
│ │ │ │ └── InternbayBackEndApplication.java
│ │ │ └── resources/
│ │ └── test/
│ ├── target/
│ ├── internbayBackEnd.iml
│ └── pom.xml
│
├── internbayFrontEnd/
│ ├── css/
│ ├── js/
│ ├── lib/
│ ├── pages/
│ └── index.html
│
├── LICENSE
├── README.md
├── internbay-job-portal.iml
```
## ⚙️ Setup Instructions  

### 1. Clone Repository

git clone https://github.com/hirunadissanayake/InternBay-job-portal.git

cd internbay

### 2. Backend Setup (Spring Boot)

Import project into IntelliJ IDEA (Maven project).

Configure database in application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/internbay
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
jwt.secret=your256bitsecretkey


Run the backend:

mvn spring-boot:run

## 📸 UI Screenshots

### Main Page
![Main Page](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-main-html-2025-09-19-23_01_45.png)  


### Find Internship
![Find Internship](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-findJob-html-2025-09-19-23_03_40.png)  


### About Us
![About Us](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-aboutUs-html-2025-09-19-23_04_11.png)  


### Contact Us
![Contact Us](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-contactUs-html-2025-09-19-23_06_29.png)  


### ResumeChecker
![ResumeChecker](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-resumeChecker-html-2025-09-19-23_06_45.png)  


### Employer Profile
![Employer Profile](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-employerProfile-html-2025-09-20-01_21_01.png)


### Candidate profile
![Candidate profile](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-candidateProfile-html-2025-09-20-01_19_22.png)


### Post Job (Employer only)
![Post Job](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-jobPosting-html-2025-09-20-00_22_07.png)  


### Manage Job (Employer only)
![Manage Job](assets/internbay-ui/screencapture-localhost-63342-internbay-job-portal-internbayFrontEnd-pages-manageJobPosting-html-2025-09-20-00_22_35.png)  


### Apply Job (Candidate only)
![Apply Job]()


### Job Details Card 
![Job Details]()


## 🎥 Demo Video
[Watch on YouTube]()