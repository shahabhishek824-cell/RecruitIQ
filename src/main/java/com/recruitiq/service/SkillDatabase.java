package com.recruitiq.service;

import java.util.*;

public class SkillDatabase {

    public static final Map<String, List<String>> CATEGORIES = new LinkedHashMap<String, List<String>>();

    static {
        CATEGORIES.put("Programming Languages", Arrays.asList(
            "Python","Java","JavaScript","TypeScript","C++","C#","Go","Rust",
            "Kotlin","Swift","PHP","Ruby","Scala","R","Dart","Perl"
        ));
        CATEGORIES.put("Web Frameworks", Arrays.asList(
            "React","Angular","Vue","Spring Boot","Django","Flask","Node.js",
            "Express","Next.js","Laravel","FastAPI","Svelte","NestJS"
        ));
        CATEGORIES.put("Data and ML", Arrays.asList(
            "TensorFlow","PyTorch","scikit-learn","Pandas","NumPy","Keras",
            "OpenCV","Spark","Hadoop","Kafka","Airflow","Power BI","Tableau","dbt"
        ));
        CATEGORIES.put("Cloud and DevOps", Arrays.asList(
            "AWS","Azure","GCP","Docker","Kubernetes","Terraform","Ansible",
            "Jenkins","GitHub Actions","CI/CD","Linux","Nginx","Helm","ArgoCD"
        ));
        CATEGORIES.put("Databases", Arrays.asList(
            "MySQL","PostgreSQL","MongoDB","Redis","Elasticsearch","Oracle",
            "SQL Server","DynamoDB","Cassandra","SQLite","MariaDB","Neo4j"
        ));
        CATEGORIES.put("Mobile Development", Arrays.asList(
            "Android","iOS","Flutter","React Native","Kotlin","Swift","Xamarin","Ionic"
        ));
        CATEGORIES.put("Design and UI", Arrays.asList(
            "Figma","Adobe XD","Photoshop","Illustrator","CSS","Tailwind",
            "Bootstrap","SASS","Material UI","Ant Design"
        ));
        CATEGORIES.put("Practices", Arrays.asList(
            "Agile","Scrum","TDD","BDD","REST","GraphQL","Microservices",
            "DevOps","Git","JIRA","OOP","Design Patterns","Kanban"
        ));
    }

    public static List<String> getAllSkills() {
        List<String> all = new ArrayList<String>();
        for (List<String> skills : CATEGORIES.values()) {
            all.addAll(skills);
        }
        return all;
    }
}
