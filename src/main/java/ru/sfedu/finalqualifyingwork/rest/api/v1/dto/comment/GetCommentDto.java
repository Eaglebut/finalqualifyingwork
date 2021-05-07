package ru.sfedu.finalqualifyingwork.rest.api.v1.dto.comment;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sfedu.finalqualifyingwork.model.Comment;
import ru.sfedu.finalqualifyingwork.rest.api.v1.dto.user.PublicUserDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ApiModel
public class GetCommentDto {

  private long id;
  private PublicUserDto author;
  private Date created;
  private Date lastUpdated;
  private List<GetCommentDto> commentList = new ArrayList<>();
  private String text;
  private boolean isEdited;

  public GetCommentDto(Comment comment) {
    id = comment.getId();
    author = new PublicUserDto(comment.getAuthor());
    commentList = comment.getCommentList()
            .stream()
            .map(GetCommentDto::new)
            .collect(Collectors.toList());
    text = comment.getText();
    isEdited = comment.isEdited();
    created = comment.getCreated();
    lastUpdated = comment.getLastUpdated();
  }

  public Comment toComment() {
    Comment comment = new Comment();
    comment.setId(id);
    comment.setAuthor(author.toUser());
    comment.setCommentList(commentList.stream().map(GetCommentDto::toComment).toList());
    comment.setText(text);
    comment.setEdited(isEdited);
    comment.setCreated(created);
    comment.setLastUpdated(lastUpdated);
    return comment;
  }
}
