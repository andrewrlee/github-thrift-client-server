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

service PushService {
  i32 getTotalNumberOfPushes();
  string ping();
  set<Push> getPushes(1:string query);
} 

