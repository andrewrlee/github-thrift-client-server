namespace java github.thrift.mongo.core.api

struct Commit {
  1: string user;
  2: string message;
  3: string sha;
}

struct Push {
  1: string      user;
  2: string      repo;
  3: i64         occurredat;
  4: set<Commit> commits;
}

exception PushError {
  1: string message;
  2: string sha;
}

service PushService {
  string ping();
  bool addPush(1:Push push) throws (1:PushError pushError);
} 

