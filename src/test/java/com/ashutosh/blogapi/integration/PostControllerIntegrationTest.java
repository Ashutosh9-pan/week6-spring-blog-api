package com.ashutosh.blogapi.integration;

import com.ashutosh.blogapi.entity.Category;
import com.ashutosh.blogapi.entity.Post;
import com.ashutosh.blogapi.repository.CategoryRepository;
import com.ashutosh.blogapi.repository.CommentRepository;
import com.ashutosh.blogapi.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Category category;

    @BeforeEach
    void setUp() {

        commentRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();

        Category newCategory = new Category();
        newCategory.setName("Technology");
        newCategory.setDescription("Technology articles");

        category = categoryRepository.save(newCategory);
    }

    @Test
    void shouldCreatePostSuccessfully() throws Exception {

        String requestBody = """
                {
                    "title": "Spring Boot Integration Test",
                    "content": "Testing REST API using MockMvc.",
                    "author": "Ashutosh",
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("Spring Boot Integration Test"))
                .andExpect(jsonPath("$.author")
                        .value("Ashutosh"))
                .andExpect(jsonPath("$.categoryId")
                        .value(category.getId()))
                .andExpect(jsonPath("$.categoryName")
                        .value("Technology"));
    }

    @Test
    void shouldReturnAllPostsWithPagination() throws Exception {

        String requestBody = """
                {
                    "title": "Pagination Test Post",
                    "content": "Testing pagination endpoint.",
                    "author": "Ashutosh",
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/posts")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "createdAt,desc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title")
                        .value("Pagination Test Post"));
    }

    @Test
    void shouldReturnBadRequestForInvalidPost() throws Exception {

        String requestBody = """
                {
                    "title": "",
                    "content": "",
                    "author": "",
                    "categoryId": null
                }
                """;

        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation Failed"));
    }

    @Test
    void shouldReturnNotFoundForMissingPost() throws Exception {

        mockMvc.perform(get("/api/posts/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldReturnPostsByCategory() throws Exception {

        String requestBody = """
                {
                    "title": "Category Filter Test",
                    "content": "Testing posts by category.",
                    "author": "Ashutosh",
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/posts/category/" + category.getId())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].categoryId")
                        .value(category.getId()));
    }

    @Test
    void shouldCreateCommentForPost() throws Exception {

        Post post = createTestPost();

        String requestBody = """
                {
                    "author": "Rahul",
                    "content": "Great article!",
                    "postId": %d
                }
                """.formatted(post.getId());

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("Rahul"))
                .andExpect(jsonPath("$.content").value("Great article!"))
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.approved").value(false));
    }

    @Test
    void shouldGetCommentsForPost() throws Exception {

        Post post = createTestPost();

        String requestBody = """
                {
                    "author": "Rahul",
                    "content": "Comment for GET test",
                    "postId": %d
                }
                """.formatted(post.getId());

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/posts/" + post.getId() + "/comments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].author").value("Rahul"))
                .andExpect(jsonPath("$[0].postId").value(post.getId()));
    }

    @Test
    void shouldUpdateComment() throws Exception {

        Post post = createTestPost();

        String createRequest = """
                {
                    "author": "Rahul",
                    "content": "Original comment",
                    "postId": %d
                }
                """.formatted(post.getId());

        String response =
                mockMvc.perform(
                                post("/api/posts/" + post.getId() + "/comments")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createRequest)
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Long commentId =
                Long.valueOf(
                        response.replaceAll(
                                ".*\"id\":(\\d+).*",
                                "$1"
                        )
                );

        String updateRequest = """
                {
                    "author": "Rahul Updated",
                    "content": "Updated comment content",
                    "postId": %d
                }
                """.formatted(post.getId());

        mockMvc.perform(
                        put("/api/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author")
                        .value("Rahul Updated"))
                .andExpect(jsonPath("$.content")
                        .value("Updated comment content"));
    }

    @Test
    void shouldDeleteComment() throws Exception {

        Post post = createTestPost();

        String createRequest = """
                {
                    "author": "Rahul",
                    "content": "Comment to delete",
                    "postId": %d
                }
                """.formatted(post.getId());

        String response =
                mockMvc.perform(
                                post("/api/posts/" + post.getId() + "/comments")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createRequest)
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Long commentId =
                Long.valueOf(
                        response.replaceAll(
                                ".*\"id\":(\\d+).*",
                                "$1"
                        )
                );

        mockMvc.perform(
                        delete("/api/comments/" + commentId)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        delete("/api/comments/" + commentId)
                )
                .andExpect(status().isNotFound());
    }

    private Post createTestPost() {

        Post post = new Post();
        post.setTitle("Comment Test Post");
        post.setContent("Post created for comment integration tests.");
        post.setAuthor("Ashutosh");
        post.setCategory(category);

        return postRepository.save(post);
    }
}