package com.example.blog.docs;

import com.example.blog.dto.PostDto;
import com.example.blog.service.PostService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class ApiDocumentationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostService serwisPostow;

	@Test
	void dokumentujeListePostow() throws Exception {
		PostDto danePosta = new PostDto();
		danePosta.setId(1L);
		danePosta.setTytul("Testowy post");
		danePosta.setTresc("<p>Tresc</p>");
		Page<PostDto> strona = new PageImpl<>(List.of(danePosta), PageRequest.of(0, 20), 1);

		given(serwisPostow.pobierzWszystkie(any(Pageable.class))).willReturn(strona);

		mockMvc.perform(get("/api/v1/posts").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("posts-list"));
	}
}
