package joelbits.modules.preprocessing.utils;

import com.google.protobuf.Timestamp;
import joelbits.model.project.protobuf.ProjectProtos.ChangedFile;
import joelbits.model.project.protobuf.ProjectProtos.Revision;
import joelbits.model.project.protobuf.ProjectProtos.Person;
import joelbits.model.project.protobuf.ProjectProtos.Project;
import joelbits.model.project.protobuf.ProjectProtos.Project.ProjectType;
import joelbits.model.project.protobuf.ProjectProtos.CodeRepository;
import joelbits.model.project.protobuf.ProjectProtos.CodeRepository.RepositoryType;
import joelbits.model.utils.FileNameUtils;

import java.util.List;

/**
 * Creates the Project nodes used by the framework (persisting and mining).
 */
public final class ProjectNodeCreator {
    public Revision createRevision(String revisionId, List<ChangedFile> revisionFiles, Person committer, int commitTime, String log) {
        return Revision.newBuilder()
                .setCommitDate(timestamp(commitTime))
                .setId(revisionId)
                .setCommitter(committer)
                .setLog(log)
                .addAllFiles(revisionFiles)
                .build();
    }

    private Timestamp timestamp(int seconds) {
        return Timestamp.newBuilder().setSeconds(seconds).build();
    }

    public Person committer(String username, String email) {
        return committer(username, username, email);
    }

    public Person committer(String username, String realName, String email) {
        return Person.newBuilder()
                .setEmail(email)
                .setUsername(username)
                .setRealName(realName)
                .build();
    }

    public Project project(String name, int createdTime, String id, String url, ProjectType type, List<CodeRepository> repositories, List<String> languages, int forks, int watchers) {
        return Project.newBuilder()
                .setId(id)
                .setName(name)
                .setType(type)
                .setUrl(url)
                .setCreatedDate(timestamp(createdTime))
                .addAllRepositories(repositories)
                .addAllProgrammingLanguages(languages)
                .setForks(forks)
                .setWatchers(watchers)
                .build();
    }

    public CodeRepository repository(String url, RepositoryType type, List<Revision> revisions) {
        return CodeRepository.newBuilder()
                .setUrl(url)
                .setType(type)
                .addAllRevisions(revisions)
                .build();
    }

    public ChangedFile changedFile(String fileName, String changeType) {
        String fileType = FileNameUtils.getExtension(fileName);
        return ChangedFile.newBuilder()
                .setName(fileName)
                .setType(ChangedFile.FileType.valueOf(fileType.toUpperCase()))
                .setChange(ChangedFile.ChangeType.valueOf(convertChangeType(changeType)))
                .build();
    }

    /**
     * Since some sources may have different ChangeTypes than used in the Protocol Buffer, they have to
     * be mapped to corresponding ChangeType.
     *
     * @param type          the ChangeType of the parsed source
     * @return              the ChangeType used in the Project Protocol Buffer message
     */
    private String convertChangeType(String type) {
        switch (type.toUpperCase()) {
            case "MODIFY":
                return "MODIFIED";
            case "ADD":
                return "ADDED";
            case "DELETE":
                return "DELETED";
            default:
                return type.toUpperCase();
        }
    }
}
