package com.github.avarabyeu.router;

import com.github.avarabyeu.restendpoint.serializer.json.GsonSerializer;
import com.google.common.collect.ImmutableMap;

/**
 * Created by avarabyeu on 12/18/15.
 */
public class RouterTest {

    public static void main(String[] args) {
        GsonSerializer serializer = new GsonSerializer();
        Endpoint e = new Endpoint(Router.builder()
                .path("/version", (rq, rs) -> rs.content("hello world")
                        .content("\n")
                        .content(rq.getRequestUri())
                        .content("\n")
                        .content(rq.raw().getRequestURL().toString()))
                //.statics(FileSystems.getDefault().getPath("/Users/avarabyeu/Downloads"))
                .path(Request.Method.POST, "/postWithBody",
                        new SerializerHandler<>(serializer, String.class,
                                (rq, rs, body) -> ImmutableMap.builder().put("key", "value").build()))
                .path(null, "/post", new SerializerHandler<>(serializer,
                        ((rq, rs) -> ImmutableMap.builder().put("key", "value").build())))
                .onException(HandlerNotFoundException.class,
                        (e1, response) -> response.statusCode(404).content("fucking shit!")).build()
        );
        e.startAsync();
    }
}
