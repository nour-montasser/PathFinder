package org.example.pathfinder.Service;

import org.example.pathfinder.Model.*;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CVService implements Services2<CV> {
    private final Connection connection;
    private long loggedInUserId = LoggedUser.getInstance().getUserId();
    public CVService() {
        this.connection = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(CV cv) {
        // ✅ Ensure the title is unique before inserting
        String uniqueTitle = generateUniqueTitle(cv.getTitle(), cv.getUserId());

        String query = "INSERT INTO CV (id_user, title, user_title, introduction, skills, date_creation, last_viewed, favorite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Math.toIntExact(loggedInUserId));
            statement.setString(2, uniqueTitle); // 🔹 Ensure unique title
            statement.setString(3, cv.getUserTitle()); // 🔥 Insert user_title field
            statement.setString(4, cv.getIntroduction());
            statement.setString(5, cv.getSkills());
            statement.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // ✅ Set creation date
            statement.setTimestamp(7, new Timestamp(System.currentTimeMillis())); // ✅ Initialize last_viewed
            statement.setBoolean(8, false); // 🔥 Explicitly set favorite to false

            statement.executeUpdate();
            System.out.println("✅ CV added successfully with unique title.");
        } catch (Exception e) {
            System.err.println("❌ Error adding CV: " + e.getMessage());
        }
    }


    private String generateUniqueTitle(String baseTitle, int userId) {
        String newTitle = baseTitle;
        int copyNumber = 1;

        String checkQuery = "SELECT COUNT(*) FROM CV WHERE title = ? AND id_user = ?";

        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            do {
                statement.setString(1, newTitle);
                statement.setInt(1, Math.toIntExact(loggedInUserId));
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // If the title exists, generate a new one
                    copyNumber++;
                    newTitle = baseTitle + " (" + copyNumber + ")";
                } else {
                    break; // Exit loop when a unique title is found
                }
            } while (true);
        } catch (Exception e) {
            System.err.println("❌ Error generating unique title: " + e.getMessage());
        }

        return newTitle;
    }



    @Override
    public void update(CV cv) {
        String query = "UPDATE CV SET id_user = ?, user_title = ?, introduction = ?, skills = ? WHERE id_cv = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Math.toIntExact(loggedInUserId));
            statement.setString(2, cv.getUserTitle());
            statement.setString(3, cv.getIntroduction());
            statement.setString(4, cv.getSkills());
            statement.setInt(5, cv.getIdCV());
            statement.executeUpdate();
            System.out.println("CV updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating CV: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM CV WHERE id_cv = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("CV deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting CV: " + e.getMessage());
        }
    }

    @Override
    public CV getById(int id) {
        String queryCV = "SELECT * FROM CV WHERE id_cv = ?";
        String queryExperiences = "SELECT * FROM experience WHERE id_cv = ?";
        String queryCertificates = "SELECT * FROM certificates WHERE id_cv = ?";
        String queryLanguages = "SELECT * FROM languages WHERE id_cv = ?";

        try (PreparedStatement statementCV = connection.prepareStatement(queryCV);
             PreparedStatement statementExp = connection.prepareStatement(queryExperiences);
             PreparedStatement statementCert = connection.prepareStatement(queryCertificates);
             PreparedStatement statementLang = connection.prepareStatement(queryLanguages)) {

            // Fetch the CV
            statementCV.setInt(1, id);
            ResultSet resultSetCV = statementCV.executeQuery();
            if (resultSetCV.next()) {
                CV cv = new CV(
                        resultSetCV.getInt("id_cv"),
                        resultSetCV.getInt("id_user"),
                        resultSetCV.getString("user_title"),
                        resultSetCV.getString("introduction"),
                        resultSetCV.getString("skills"),
                        resultSetCV.getTimestamp("date_creation"),
                        resultSetCV.getTimestamp("last_viewed") // ✅ Fetch lastViewed
                );
                cv.setTitle(resultSetCV.getString("title"));

                // 🔹 Update `last_viewed` every time the CV is retrieved
                updateLastViewed(id);

                // Fetch associated experiences
                statementExp.setInt(1, id);
                ResultSet resultSetExp = statementExp.executeQuery();
                while (resultSetExp.next()) {
                    Experience experience = new Experience(
                            resultSetExp.getInt("id_experience"),
                            resultSetExp.getInt("id_cv"),
                            resultSetExp.getString("type"),
                            resultSetExp.getString("position"),
                            resultSetExp.getString("location_name"),
                            resultSetExp.getString("start_date"),
                            resultSetExp.getString("end_date"),
                            resultSetExp.getString("description")
                    );
                    cv.addExperience(experience);
                }

                // Fetch associated certificates
                statementCert.setInt(1, id);
                ResultSet resultSetCert = statementCert.executeQuery();
                while (resultSetCert.next()) {
                    Certificate certificate = new Certificate(
                            resultSetCert.getInt("id_certificate"),
                            resultSetCert.getInt("id_cv"),
                            resultSetCert.getString("title"),
                            resultSetCert.getString("description"),
                            resultSetCert.getString("media"),
                            resultSetCert.getString("issued_by"),
                            resultSetCert.getDate("issue_date")
                    );
                    cv.addCertificate(certificate);
                }

                // Fetch associated languages
                statementLang.setInt(1, id);
                ResultSet resultSetLang = statementLang.executeQuery();
                while (resultSetLang.next()) {
                    Language language = new Language(
                            resultSetLang.getInt("id_language"),
                            resultSetLang.getInt("id_cv"),
                            resultSetLang.getString("language_name"),
                            resultSetLang.getString("level")
                    );
                    cv.addLanguage(language);
                }

                return cv; // ✅ Return CV with all associated data
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving CV: " + e.getMessage());
        }
        return null;
    }
    public void updateLastViewed(int cvId) {
        String query = "UPDATE CV SET last_viewed = ? WHERE id_cv = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // 🔥 Set current timestamp
            statement.setInt(2, cvId);

            statement.executeUpdate();
            System.out.println("✅ Last viewed timestamp updated for CV ID: " + cvId);
        } catch (Exception e) {
            System.err.println("❌ Error updating last viewed: " + e.getMessage());
        }
    }
    public void updateFavorite(int cvId) {
        String query = "UPDATE CV SET favorite = NOT favorite WHERE id_cv = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cvId); // Set the CV ID
            statement.executeUpdate();
            System.out.println("✅ Favorite status toggled for CV ID: " + cvId);
        } catch (Exception e) {
            System.err.println("❌ Error toggling favorite status: " + e.getMessage());
        }
    }



    @Override
    public List<CV> getAll() {
        List<CV> cvs = new ArrayList<>();
        String queryCV = "SELECT * FROM CV";
        String queryExperiences = "SELECT * FROM experience WHERE id_cv = ?";

        try (PreparedStatement statementCV = connection.prepareStatement(queryCV);
             PreparedStatement statementExp = connection.prepareStatement(queryExperiences)) {

            ResultSet resultSetCV = statementCV.executeQuery();
            while (resultSetCV.next()) {
                CV cv = new CV(
                        resultSetCV.getInt("id_cv"),
                        resultSetCV.getInt("id_user"),
                        resultSetCV.getString("title"),
                        resultSetCV.getString("introduction"),
                        resultSetCV.getString("skills"),
                        resultSetCV.getTimestamp("date_creation"),
                        resultSetCV.getTimestamp("last_viewed") // ✅ Fetch lastViewed
                );

                // Fetch associated experiences for this CV
                statementExp.setInt(1, cv.getIdCV());
                ResultSet resultSetExp = statementExp.executeQuery();
                while (resultSetExp.next()) {
                    Experience experience = new Experience(
                            resultSetExp.getInt("id_experience"),
                            resultSetExp.getInt("id_cv"),
                            resultSetExp.getString("type"),
                            resultSetExp.getString("position"),
                            resultSetExp.getString("location_name"),
                            resultSetExp.getString("start_date"),
                            resultSetExp.getString("end_date"),
                            resultSetExp.getString("description")
                    );
                    cv.addExperience(experience);
                }

                cvs.add(cv);
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving CVs: " + e.getMessage());
        }
        return cvs;
    }

    public int getLatestCVId() {
        String query = "SELECT MAX(id_cv) FROM CV";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1); // Return the latest inserted CV ID
            }
        } catch (Exception e) {
            System.err.println("Error retrieving latest CV ID: " + e.getMessage());
        }
        return -1; // Return -1 if an error occurs
    }

    public List<CV> getCVsByUserId(int userId) {
        List<CV> cvs = new ArrayList<>();
        String query = "SELECT id_cv, title, date_creation, last_viewed, favorite FROM CV WHERE id_user = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                CV cv = new CV(
                        resultSet.getInt("id_cv"),
                        userId,
                        resultSet.getString("title"),
                        "", // No introduction needed
                        "", // No skills needed
                        resultSet.getTimestamp("date_creation"),
                        resultSet.getTimestamp("last_viewed")
                );

                // ✅ Set the favorite status from DB
                cv.setFavorite(resultSet.getBoolean("favorite"));

                cvs.add(cv);
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving CVs for user: " + e.getMessage());
        }
        return cvs;
    }



    public void makeCopyOfCV(int originalCvId) {
        ExperienceService experienceService = new ExperienceService();
        LanguageService languageService = new LanguageService();
        CertificateService certificateService = new CertificateService();

        // 🔥 Retrieve the original CV from the database
        CV originalCV = getById(originalCvId);

        if (originalCV == null) {
            System.err.println("❌ Original CV not found. Copy operation aborted.");
            return;
        }

        // ✅ Generate a Unique Title
        String newTitle = generateUniqueTitle(originalCV.getTitle(), originalCV.getUserId());

        // ✅ Create a duplicate CV with a unique title
        CV copiedCV = new CV(originalCV);

        // ✅ Insert the new CV into the database
        add(copiedCV);

        // 🔥 Retrieve the newly inserted CV's ID
        int newCvId = getLatestCVId();
        if (newCvId == -1) {
            System.err.println("❌ Failed to retrieve the new CV ID.");
            return;
        }

        // ✅ Copy Experiences
        List<Experience> originalExperiences = experienceService.getByCvId(originalCvId);
        for (Experience exp : originalExperiences) {
            Experience copiedExp = new Experience(
                    0, newCvId, exp.getType(), exp.getPosition(), exp.getLocationName(),
                    exp.getStartDate(), exp.getEndDate(), exp.getDescription()
            );
            experienceService.add(copiedExp);
        }

        // ✅ Copy Certificates
        List<Certificate> originalCertificates = certificateService.getByCvId(originalCvId);
        for (Certificate cert : originalCertificates) {
            Certificate copiedCert = new Certificate(
                    0, newCvId, cert.getTitle(), cert.getDescription(), cert.getMedia(),
                    cert.getAssociation(), cert.getDate()
            );
            certificateService.add(copiedCert);
        }

        // ✅ Copy Languages
        List<Language> originalLanguages = languageService.getByCvId(originalCvId);
        for (Language lang : originalLanguages) {
            Language copiedLang = new Language(
                    0, newCvId, lang.getName(), lang.getLevel()
            );
            languageService.add(copiedLang);
        }

        System.out.println("✅ CV Copy Successful! New CV ID: " + newCvId);
    }
    public List<CV> getAllCVsWithDetails() {
        List<CV> cvList = new ArrayList<>();

        String query = """
        SELECT cv.id_cv, 
               u.name AS user_name, 
               cv.title, 
               cv.introduction, 
               cv.skills, 
               cv.date_creation,

               -- Format Experiences
               (SELECT GROUP_CONCAT(CONCAT(e.type, ' - ', e.position, ' at ', e.location_name, ' (', e.start_date, ' - ', e.end_date, ')') SEPARATOR ' | ')
                FROM experience e WHERE e.id_cv = cv.id_cv) AS experiences,

               -- Format Languages
               (SELECT GROUP_CONCAT(CONCAT(l.language_name, ' (', l.level, ')') SEPARATOR ' | ')  -- 🔥 FIXED COLUMN NAME
                FROM languages l WHERE l.id_cv = cv.id_cv) AS languages,

               -- Format Certificates
               (SELECT GROUP_CONCAT(CONCAT(c.title, ' - ', c.issued_by, ' (', c.issue_date, ')') SEPARATOR ' | ')
                FROM certificates c WHERE c.id_cv = cv.id_cv) AS certificates

        FROM CV cv
        JOIN app_user u ON cv.id_user = u.id_user
        ORDER BY cv.date_creation DESC;
        """;


        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                CV cv = new CV(
                        resultSet.getInt("id_cv"),
                        resultSet.getString("user_name"),  // ✅ Ensure user_name instead of ID
                        resultSet.getString("title"),
                        resultSet.getString("introduction"),
                        resultSet.getString("skills"),
                        resultSet.getTimestamp("date_creation")
                );

                // ✅ Set formatted fields safely
                cv.setFormattedExperiences(resultSet.getString("experiences"));
                cv.setFormattedLanguages(resultSet.getString("languages"));
                cv.setFormattedCertificates(resultSet.getString("certificates"));


                cvList.add(cv);
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error retrieving CVs: " + e.getMessage());
            e.printStackTrace(); // 🔥 Useful for debugging
        }
        return cvList;
    }





}
