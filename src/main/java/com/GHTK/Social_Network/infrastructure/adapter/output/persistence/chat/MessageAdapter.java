package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.CustomMessageRepository;
import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.collection.chat.ReactionMessages;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.ReactionComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.ReactionMessagesCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.MessageRepository;
import com.GHTK.Social_Network.infrastructure.mapper.MessageMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageAdapter implements MessagePort {
    private final MessageRepository messageRepository;
    private final CustomMessageRepository customMessageRepository;

    private final MessageMapperETD messageMapperETD;

    @Override
    public Message getMessageById(String messageId) {
        MessageCollection messageCollection = messageRepository.findById(messageId).orElse(null);
        if (messageCollection == null) {
            return null;
        }
        return messageMapperETD.messageCollectionToMessage(messageCollection);
    }

    @Override
    public Message saveMessage(Message newMessage) {
        return messageMapperETD.messageCollectionToMessage(
                messageRepository.save(messageMapperETD.messageToMessageCollection(newMessage))
        );
    }

    @Override
    public ReactionMessages getReactionByUserIdAndMsgId(String msgId, Long userId) {
        Optional<MessageCollection> messageCollection = messageRepository.findReactionsById(msgId, userId);
        return messageCollection.map(collection -> messageMapperETD.mapReactionMessagesCollection(collection.getReactionMsgs().get(0))).orElse(null);
    }

    @Override
    public void saveOrChangeReactionMessage(String msgId, Long userId, EReactionType reactionType) {
        System.out.println("cuu");
        MessageCollection messageCollection = messageRepository.findById(msgId).orElse(null);
        List<ReactionMessagesCollection> reactionMessagesCollections = messageCollection.getReactionMsgs();
        if (reactionMessagesCollections == null) {
            reactionMessagesCollections = new LinkedList<>();
            messageCollection.setReactionMsgs(reactionMessagesCollections);
            messageRepository.save(messageCollection);
            MessageCollection messageCollection1 = messageRepository.findById(msgId).orElse(null);

            ReactionMessagesCollection reactionMessagesCollection = new ReactionMessagesCollection(
                    EReactionTypeEntity.LIKE,
                    userId
            );
            reactionMessagesCollections.add(reactionMessagesCollection);
            messageCollection1.getReactionMsgs().add(reactionMessagesCollection);
            messageRepository.save(messageCollection1);
            MessageCollection savedMessage = messageRepository.findById(messageCollection.getMsgId()).orElse(null);
            System.out.println("Saved Message 1: " + savedMessage);
            return;
        }

        for (int i = 0; i < reactionMessagesCollections.size(); i++) {
            if (reactionMessagesCollections.get(i).getUserId().equals(userId)) {
                reactionMessagesCollections.get(i).setReactionType(EReactionTypeEntity.LIKE);
                messageCollection.setReactionMsgs(reactionMessagesCollections);
                messageRepository.save(messageCollection);
                MessageCollection savedMessage = messageRepository.findById(messageCollection.getMsgId()).orElse(null);
                System.out.println("Saved Message 2: " + savedMessage);
                return;
            }
        }

//        reactionMessagesCollections.add(new ReactionMessagesCollection(
//                EReactionTypeEntity.LIKE,
//                userId
//        ));
        messageCollection.getReactionMsgs().add(
                new ReactionMessagesCollection(
                        EReactionTypeEntity.LIKE,
                        userId
                )
        );
//    messageRepository.save(messageCollection);
        messageRepository.save(messageCollection);
        MessageCollection savedMessage = messageRepository.findById(messageCollection.getMsgId()).orElse(null);
        System.out.println("Saved Message 3: " + savedMessage);

    }

    @Override
    public void deleteReactionMessage(String msgId, Long userId) {
        customMessageRepository.deleteReactionMessageCustom(msgId, userId);
    }
}
