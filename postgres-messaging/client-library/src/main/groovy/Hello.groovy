import com.zenika.talk.Channel
import com.zenika.talk.Message
import com.zenika.talk.MessageConverter
import com.zenika.talk.MessageHandler
import groovy.transform.Immutable

import java.util.concurrent.Executors

@Immutable
class Greeting {
    String hello
}

class Hello {

    static void main(String[] args) {
        def threadPool = Executors.newFixedThreadPool(1);

        threadPool.execute(new Runnable() {
            @Override
            void run() {
                new Channel<Greeting>(
                        'jdbc:postgresql://localhost:5432/postgres',
                        'postgres',
                        'postgres',
                        new MessageHandler<Greeting>() {
                            @Override
                            void handleMessage(Message<Greeting> message) {
                                println(message.payload)
                            }
                        },
                        new MessageConverter<Greeting>() {
                            @Override
                            Greeting convert(Map<String, Object> payload) {
                                return new Greeting(payload["hello"])
                            }
                        }
                )
            }
        })

        Thread.sleep(100_000)
    }
}