package com.example.campusCircle.config;

import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Channel.ChannelCategory;
import com.example.campusCircle.model.Post;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.model.nosql.PostContent;
import com.example.campusCircle.repository.ChannelRepository;
import com.example.campusCircle.repository.PostRepository;
import com.example.campusCircle.repository.PostContentRepository;
import com.example.campusCircle.repository.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ChannelRepository channelRepository;
    private final PostRepository postRepository;
    private final PostContentRepository postContentRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ChannelRepository channelRepository,
                           PostRepository postRepository,
                           PostContentRepository postContentRepository,
                           UsersRepository usersRepository,
                           PasswordEncoder passwordEncoder) {
        this.channelRepository = channelRepository;
        this.postRepository = postRepository;
        this.postContentRepository = postContentRepository;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create users first if none exist
        if (usersRepository.count() == 0) {
            System.out.println("Creating seed users...");
            createUsers();
        }

        // Only seed channels if no channels exist
        if (channelRepository.count() > 0) {
            System.out.println("Channels already exist, skipping channel seeding.");
            return;
        }

        System.out.println("Seeding initial data...");

        // Create Channels
        List<Channel> channels = createChannels();
        
        // Create Posts for each channel
        createPosts(channels);

        System.out.println("Data seeding completed!");
    }

    private void createUsers() {
        LocalDateTime now = LocalDateTime.now();

        // Admin user
        Users admin = new Users();
        admin.setUsername("admin");
        admin.setEmail("admin@university.edu");
        admin.setPasswordHash(passwordEncoder.encode("password123"));
        admin.setRealName("System Admin");
        admin.setVerificationStatus(Users.VerificationStatus.VERIFIED);
        admin.setIsActive(true);
        admin.setCreatedAt(now.minusDays(60));
        usersRepository.save(admin);

        // Test users with university emails
        String[] usernames = {"alex_dev", "sarah_cs", "mike_student", "emma_watson", "john_doe"};
        String[] realNames = {"Alex Johnson", "Sarah Chen", "Mike Wilson", "Emma Watson", "John Doe"};
        String[] emails = {
            "alex.johnson@mit.edu",
            "sarah.chen@stanford.edu",
            "mike.wilson@berkeley.edu",
            "emma.watson@harvard.edu",
            "john.doe@ucla.edu"
        };

        for (int i = 0; i < usernames.length; i++) {
            Users user = new Users();
            user.setUsername(usernames[i]);
            user.setEmail(emails[i]);
            user.setPasswordHash(passwordEncoder.encode("password123"));
            user.setRealName(realNames[i]);
            user.setVerificationStatus(Users.VerificationStatus.VERIFIED);
            user.setIsActive(true);
            user.setCreatedAt(now.minusDays(30 - i * 5));
            usersRepository.save(user);
        }

        System.out.println("Created " + (usernames.length + 1) + " seed users");
    }

    private List<Channel> createChannels() {
        LocalDateTime now = LocalDateTime.now();

        Channel channel1 = new Channel();
        channel1.setName("Computer Science Help");
        channel1.setDescription("Get help with programming, algorithms, data structures, and all CS topics. Share resources and collaborate on projects.");
        channel1.setRules("1. Be respectful\n2. Use code blocks for code\n3. No homework cheating\n4. Search before posting");
        channel1.setCategory(ChannelCategory.ACADEMICS);
        channel1.setSubscriberCount(1250);
        channel1.setIsActive(true);
        channel1.setCreatedAt(now.minusDays(30));
        channel1.setCreatedBy("admin");

        Channel channel2 = new Channel();
        channel2.setName("Mental Health Support");
        channel2.setDescription("A safe space to discuss mental health, stress management, and wellness tips. We're here for each other.");
        channel2.setRules("1. Be supportive\n2. No judgment\n3. Seek professional help for serious issues\n4. Trigger warnings required");
        channel2.setCategory(ChannelCategory.MENTAL_HEALTH);
        channel2.setSubscriberCount(890);
        channel2.setIsActive(true);
        channel2.setCreatedAt(now.minusDays(25));
        channel2.setCreatedBy("admin");

        Channel channel3 = new Channel();
        channel3.setName("Career & Internships");
        channel3.setDescription("Share internship opportunities, resume tips, interview experiences, and career advice.");
        channel3.setRules("1. Verify job postings\n2. No MLM/scams\n3. Be helpful to newcomers\n4. Share interview experiences");
        channel3.setCategory(ChannelCategory.CAREER);
        channel3.setSubscriberCount(2100);
        channel3.setIsActive(true);
        channel3.setCreatedAt(now.minusDays(28));
        channel3.setCreatedBy("admin");

        Channel channel4 = new Channel();
        channel4.setName("Campus Events");
        channel4.setDescription("Stay updated on campus events, club activities, workshops, and social gatherings.");
        channel4.setRules("1. Include date and location\n2. No spam\n3. Update if event is cancelled\n4. Be accurate");
        channel4.setCategory(ChannelCategory.CAMPUS_LIFE);
        channel4.setSubscriberCount(3200);
        channel4.setIsActive(true);
        channel4.setCreatedAt(now.minusDays(35));
        channel4.setCreatedBy("admin");

        Channel channel5 = new Channel();
        channel5.setName("Study Groups");
        channel5.setDescription("Find study partners, create study groups, and collaborate on coursework.");
        channel5.setRules("1. Specify course/subject\n2. Include meeting times\n3. Be reliable\n4. No cheating");
        channel5.setCategory(ChannelCategory.ACADEMICS);
        channel5.setSubscriberCount(1500);
        channel5.setIsActive(true);
        channel5.setCreatedAt(now.minusDays(20));
        channel5.setCreatedBy("admin");

        Channel channel6 = new Channel();
        channel6.setName("Memes & Fun");
        channel6.setDescription("The place for campus memes, jokes, and light-hearted content. Keep it clean!");
        channel6.setRules("1. Keep it appropriate\n2. No offensive content\n3. Credit creators\n4. Have fun!");
        channel6.setCategory(ChannelCategory.ENTERTAINMENT);
        channel6.setSubscriberCount(4500);
        channel6.setIsActive(true);
        channel6.setCreatedAt(now.minusDays(40));
        channel6.setCreatedBy("admin");

        Channel channel7 = new Channel();
        channel7.setName("Housing & Roommates");
        channel7.setDescription("Find roommates, sublets, and housing advice for students.");
        channel7.setRules("1. Verify listings\n2. No scams\n3. Be respectful\n4. Include pricing");
        channel7.setCategory(ChannelCategory.MARKETPLACE);
        channel7.setSubscriberCount(980);
        channel7.setIsActive(true);
        channel7.setCreatedAt(now.minusDays(22));
        channel7.setCreatedBy("admin");

        Channel channel8 = new Channel();
        channel8.setName("Sports & Fitness");
        channel8.setDescription("Discuss sports, find workout buddies, and share fitness tips.");
        channel8.setRules("1. Be supportive\n2. No body shaming\n3. Share verified info\n4. Be inclusive");
        channel8.setCategory(ChannelCategory.SOCIAL);
        channel8.setSubscriberCount(1100);
        channel8.setIsActive(true);
        channel8.setCreatedAt(now.minusDays(18));
        channel8.setCreatedBy("admin");

        return channelRepository.saveAll(List.of(
                channel1, channel2, channel3, channel4, 
                channel5, channel6, channel7, channel8
        ));
    }

    private void createPosts(List<Channel> channels) {
        LocalDateTime now = LocalDateTime.now();
        
        // Use different authors for posts
        String[] authors = {"alex_dev", "sarah_cs", "mike_student", "emma_watson", "john_doe", "admin"};

        // Channel 1: CS Help posts
        createPostWithContent(
                channels.get(0).getId(), 
                authors[0],
                "Best resources for learning Data Structures?",
                "Hey everyone! I'm starting my DSA course and looking for good resources. I've heard about LeetCode but not sure where to start. Any recommendations for beginners?",
                45, 2, 12, now.minusHours(5)
        );

        createPostWithContent(
                channels.get(0).getId(),
                authors[1],
                "Need help with recursion - Stack overflow issues",
                "I'm getting a stack overflow error in my recursive function. Here's my code:\n\n```java\npublic int factorial(int n) {\n    return n * factorial(n-1);\n}\n```\n\nCan someone help me fix it?",
                23, 0, 8, now.minusHours(10)
        );

        // Channel 2: Mental Health posts
        createPostWithContent(
                channels.get(1).getId(),
                authors[2],
                "Tips for managing exam stress",
                "Finals are coming up and I'm feeling overwhelmed. What are your best strategies for managing stress during exam season? I'd love to hear what works for you all.",
                67, 1, 25, now.minusHours(8)
        );

        createPostWithContent(
                channels.get(1).getId(),
                authors[3],
                "Weekly mindfulness sessions - Join us!",
                "We're starting weekly mindfulness and meditation sessions every Wednesday at 5 PM in the wellness center. All are welcome, no experience needed!",
                34, 0, 15, now.minusDays(1)
        );

        // Channel 3: Career posts
        createPostWithContent(
                channels.get(2).getId(),
                authors[4],
                "Summer 2026 Internship Thread",
                "Let's share summer internship opportunities! I'll start:\n\n- Google STEP: Open now\n- Microsoft Explore: Deadline Feb 15\n- Amazon SDE Intern: Rolling\n\nAdd more below!",
                156, 3, 45, now.minusHours(3)
        );

        createPostWithContent(
                channels.get(2).getId(),
                authors[0],
                "Resume review - Software Engineer position",
                "Would appreciate feedback on my resume. Currently a junior CS major with 1 internship experience. Link in comments.",
                28, 1, 18, now.minusHours(15)
        );

        // Channel 4: Campus Events
        createPostWithContent(
                channels.get(3).getId(),
                authors[5],
                "Hackathon this weekend! $5000 in prizes",
                "🚀 Annual Campus Hackathon\n\n📅 Date: This Saturday-Sunday\n📍 Location: Engineering Building\n💰 Prizes: $5000 total\n\nForm teams of 2-4. Register at the link in comments!",
                89, 1, 32, now.minusHours(2)
        );

        createPostWithContent(
                channels.get(3).getId(),
                authors[1],
                "Free pizza at the CS club meeting",
                "Come to the CS club meeting tonight at 7 PM in room 301. We're discussing AI projects and there will be FREE PIZZA! 🍕",
                72, 2, 8, now.minusHours(6)
        );

        // Channel 5: Study Groups
        createPostWithContent(
                channels.get(4).getId(),
                authors[2],
                "Looking for Calculus II study partners",
                "Anyone taking Calc II this semester? Looking for people to form a study group. We can meet at the library. DM me if interested!",
                18, 0, 6, now.minusHours(12)
        );

        // Channel 6: Memes
        createPostWithContent(
                channels.get(5).getId(),
                authors[3],
                "When the professor says 'this won't be on the exam'",
                "And then it's literally the first question 😭\n\n[Insert crying cat meme here]",
                234, 5, 42, now.minusHours(4)
        );

        createPostWithContent(
                channels.get(5).getId(),
                authors[4],
                "My code works and I don't know why",
                "Day 47: The code still works. I'm afraid to touch it. My professor asked me to explain it. I told him it's magic. He didn't laugh.",
                189, 3, 28, now.minusHours(20)
        );

        // Channel 7: Housing
        createPostWithContent(
                channels.get(6).getId(),
                authors[0],
                "Roommate needed - 2BR apartment near campus",
                "Looking for a roommate starting Spring semester.\n\n🏠 2BR/1BA apartment\n📍 5 min walk to campus\n💵 $650/month + utilities\n\nNon-smoking, pet-friendly. DM for details!",
                42, 0, 15, now.minusHours(8)
        );

        // Channel 8: Sports
        createPostWithContent(
                channels.get(7).getId(),
                authors[1],
                "Basketball pickup games - Looking for players",
                "We play pickup basketball every Tuesday and Thursday at 6 PM at the rec center. All skill levels welcome. Just show up!",
                38, 1, 12, now.minusDays(1)
        );

        System.out.println("Created sample posts for all channels");
    }

    private void createPostWithContent(Long channelId, String author, String title, String content,
                                        int upvotes, int downvotes, int comments, LocalDateTime createdAt) {
        Post post = Post.builder()
                .channelId(channelId)
                .authorUsername(author)
                .title(title)
                .upvoteCount(upvotes)
                .downvoteCount(downvotes)
                .commentCount(comments)
                .isPinned(false)
                .isLocked(false)
                .createdAt(createdAt)
                .build();

        Post savedPost = postRepository.save(post);

        PostContent postContent = new PostContent();
        postContent.setPostId(String.valueOf(savedPost.getId()));
        postContent.setBodyText(content);
        postContent.setViewCount((long) (upvotes * 3 + comments * 2)); // Simulate views
        postContentRepository.save(postContent);
    }
}
